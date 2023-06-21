package ru.zoommax;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

public interface PostHandler extends HttpHandler {
    @Override
    default void handle(HttpExchange exchange) throws IOException {
        String respText = "only post";
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")){
            InputStream is = exchange.getRequestBody();
            StringBuilder stringBuilder = new StringBuilder();

            new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .forEach( (String s) -> stringBuilder.append(s).append("\n"));

            respText = response(stringBuilder.toString());
        }
        exchange.sendResponseHeaders(200, respText.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(respText.getBytes());
        output.flush();
        exchange.close();
    }

    String response(String requestBody);
}
