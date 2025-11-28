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
import util.UserInteraction;
import java.io.*;
import java.util.Map;

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
           try {
               sendByteData(exchange,ResponseCodes.OK,ContentType.TEXT_HTML,getHtml("/register","Регистрация прошла успешно").getBytes( ));
               dataModel.write();
           }catch (IOException e) {
               e.printStackTrace();
           }
       }
       else {
           try {
               sendByteData(exchange,ResponseCodes.OK,ContentType.TEXT_HTML,getHtml("/register","Регистрация не прошла </br> повторите попытку").getBytes( ));
           }catch (IOException e) {
               e.printStackTrace();
           }
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
            ;
            dataModel.setClient(parsed.get("email") + parsed.get("user-password"));
            dataModel.write();
            redirect303(exchange,"/profile");
        }
        else {
            try {
                sendByteData(exchange,ResponseCodes.OK,ContentType.TEXT_HTML,getHtml("/login","вход не прошел </br> повторите попытку").getBytes( ));
            }catch (IOException e) {
                e.printStackTrace();
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

    private String getHtml(String actionPath,String result) {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Welcome</title>\n" +
                "    <link rel=\"stylesheet\" href=\"css/forms.css\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<main>\n" +
                "    <form action=\"/" + actionPath + "\" method=\"post\">\n" +
                "        <fieldset>\n" +
                "            <div class=\"legend\">\n" +
                "                <p>"+ result + "</p>\n" +
                "\n" +
                "            </div>\n" +
                "            <div class=\"form-element\">\n" +
                "                <label for=\"user-email\">email</label>\n" +
                "                <input type=\"email\" name=\"email\" id=\"user-email\" placeholder=\"your email\" required autofocus>\n" +
                "            </div>\n" +
                "            <div class=\"form-element\">\n" +
                "                <label for=\"user-password\">password</label>\n" +
                "                <input type=\"password\" name=\"user-password\" id=\"user-password\" placeholder=\"your password\" required>\n" +
                "            </div>\n" +
                "            <div class=\"hr-line\">\n" +
                "                <span class=\"details\">one more step to go</span>\n" +
                "            </div>\n" +
                "            <div class=\"form-element\">\n" +
                "                <button class=\"register-button\" type=\"submit\">Register!</button>\n" +
                "            </div>\n" +
                "        </fieldset>\n" +
                "    </form>\n" +
                "</main>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
        return html;
    }
}
