package ServerLogic;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import model.Client;
import server.BasicServer;
import server.ContentType;
import server.ResponseCodes;
import util.Decode;
import util.JsonCreateReadWrite;
import util.StringWrapper;
import util.UserInteraction;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();

    public Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/books", this::booksHandler);
        registerGet("/bookInfo", this::bookInfoHandler);
        registerGet("/profile", this::profileHandlerHandler);
        registerGet("/login", this::getLoginHandler);
        registerPost("/login", this::postLoginHandler);
        registerPost("/register", this::postRegisterHandler);
        registerGet("/register", this::getRegisterHandler);
    }

    private void getRegisterHandler(HttpExchange exchange) {
        renderTemplate(exchange, "register.html", getDataModel());
    }

    private void postRegisterHandler(HttpExchange exchange) {
        DataModel dataModel = getDataModel();
        String raw = Server.getRequestBody(exchange);
        Map<String,String> parsed = Decode.parseUrlEncoded(raw, "&");

       if(UserInteraction.registration(dataModel,parsed.get("email"),parsed.get("user-password"))) {

           renderTemplate(exchange, "register.html", StringWrapper.getWrapper("Регистрация прошла успешно"));
           dataModel.write();

       }
       else {

           renderTemplate(exchange, "register.html", StringWrapper.getWrapper("Регистрация не прошла </br> повторите попытку"));
       }
    }

    private void getLoginHandler(HttpExchange exchange) {
        renderTemplate(exchange, "index.html", getDataModel());
    }

    private void postLoginHandler(HttpExchange exchange) {
        DataModel dataModel = getDataModel();
        String raw = Server.getRequestBody(exchange);
        Map<String,String> parsed = Decode.parseUrlEncoded(raw, "&");
        if(UserInteraction.login(dataModel,parsed.get("email"),parsed.get("user-password"))) {

            dataModel.setClient(parsed.get("email") + parsed.get("user-password"));
            dataModel.write();

            Map<String, Object> data = new HashMap<>();
            Cookie mail = Cookie.make("id" ,makeId(parsed.get("email") + parsed.get("user-password")));

            mail.setHttpOnly(true);
            mail.setMaxAge(10000);
            exchange.getResponseHeaders().add("Set-Cookie", mail.toString());
            redirect303(exchange,"/profile");
        }
        else {
            renderTemplate(exchange, "index.html", StringWrapper.getWrapper("Вход не прошел повторите попытку"));
        }
    }

    private int makeId(String key) {
        java.util.random.RandomGenerator r = new Random();
        while (true) {
            int newId = r.nextInt(0,1000000);
            boolean isExist = getDataModel().getClients().entrySet().stream().anyMatch(c -> c.getValue().getId() == r.nextInt());
            if(!isExist) {
               DataModel dataModel = getDataModel();
               dataModel.getClients().get(key).setId(newId);
               dataModel.write();

                return newId;
            }
        }
    }


    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("data"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void profileHandlerHandler(HttpExchange exchange) {
        renderTemplate(exchange, "client.html", getDataModel());
    }


    private void bookInfoHandler(HttpExchange exchange) {
        renderTemplate(exchange, "bookInfo.html", getDataModel());
    }

    private void booksHandler(HttpExchange exchange) {
        renderTemplate(exchange, "books.html", getDataModel());
    }


    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {

            Template temp = freemarker.getTemplate(templateFile);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                JsonCreateReadWrite.write("data.json",getDataModel());
                temp.process(dataModel, writer);
                writer.flush();

                var data = stream.toByteArray();

                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);

            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private DataModel getDataModel() {
         DataModel dataModel = JsonCreateReadWrite.read("data.json");
        return dataModel;
    }
}
