package ServerLogic;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import server.BasicServer;
import server.ContentType;
import server.ResponseCodes;
import util.Decode;
import util.JsonCreateReadWrite;
import util.Registration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();

    public Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/books", this::booksHandler);
        registerGet("/bookInfo", this::bookInfoHandler);
        registerGet("/ClientInfo", this::clientInfoHandler);
        registerPost("/login", this::postLoginHandler);
        registerGet("/login", this::getLoginHandler);
        registerPost("/register", this::postRegisterHandler);
        registerGet("/register", this::getRegisterHandler);
        registerGet("/profile", this::getLoginHandler);
    }

    private void getRegisterHandler(HttpExchange exchange) {
        renderTemplate(exchange, "register.html", getDataModel());
    }

    private void postRegisterHandler(HttpExchange exchange) {
        String cType = getContentType(exchange);
        String raw = Server.getRequestBody(exchange);

        Map<String,String> parsed = Decode.parseUrlEncoded(raw, "&");
       if(Registration.registration(getDataModel(),parsed.get("email"),parsed.get("user-password"))) {
//            redirect303(exchange,);
       }
       else {

       }
    }

    private void getLoginHandler(HttpExchange exchange) {
        renderTemplate(exchange, "client.html", getDataModel());
    }

    private void postLoginHandler(HttpExchange exchange) {
        String cType = getContentType(exchange);
        String raw = Server.getRequestBody(exchange);

        Map<String,String> parsed = Decode.parseUrlEncoded(raw, "&");
        System.out.println(parsed.entrySet());
        String fmt = "%s %n" +
                " asd   %s%n" +
                "asdad   %s";
        String data = String.format(fmt, raw,cType, parsed);
        try {
            sendByteData(exchange,ResponseCodes.OK,ContentType.TEXT_HTML,data.getBytes( ));
        }catch (IOException e) {
            e.printStackTrace();
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

    private void clientInfoHandler(HttpExchange exchange) {
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
