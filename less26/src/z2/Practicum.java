package z2;



import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

class HelloHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response;

        // извлеките метод из запроса
        String method = httpExchange.getRequestMethod();

        switch(method) {
            case "POST":
                response = handlePostRequest(httpExchange);
            case "GET":
                response = handleGetRequest(httpExchange);
                break;
            default:
                // не забудьте про ответ для остальных методов
            response = "некорректный метод";
            httpExchange.sendResponseHeaders(405,0);
            return;
        }

        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private static String handleGetRequest(HttpExchange httpExchange) {
        // обработайте GET-запрос в соответствии с условиями задания
        return "здраствуйте";
    }

    private static String handlePostRequest(HttpExchange httpExchange) throws IOException {
        // обработайте POST-запрос в соответствии с условиями задания

        // извлеките path из запроса
        String path = httpExchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        String profession = splitStrings[2];
        String name = splitStrings[3];

        // извлеките тело запроса
        InputStream is = httpExchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        // объедините полученные данные из тела и пути запроса
        String response = body + " , " + profession +" " + name + "!";

        // извлеките заголовок и в зависимости от условий дополните
        Headers requestHeaders = httpExchange.getResponseHeaders();
        List<String> wishGoodDay = requestHeaders.get("X-Wish-Good-Day");
        if ((wishGoodDay != null) && (wishGoodDay.contains("true"))) {
            return "хорошего вам дня";
        }
        return  response;
    }
}

public class Practicum {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/hello", new HelloHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        httpServer.stop(2);
    }
}
