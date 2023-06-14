package ru.zoommax;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public interface StdHandler extends HttpHandler {
    @Override
    default void handle(HttpExchange exchange) throws IOException {
        String request = Server.decode(exchange.getRequestURI().getRawQuery());
        String respText = response(request);
        exchange.sendResponseHeaders(200, respText.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(respText.getBytes());
        output.flush();
        exchange.close();
    }

    String response(String request);
}
