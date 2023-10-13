package ru.zoommax;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public interface GetHandler extends HttpHandler {
    @Override
    default void handle(HttpExchange exchange) throws IOException {
        String clientIp = Server.getIp(exchange);
        String request = Server.decode(exchange.getRequestURI().getRawQuery());
        HashMap<String,String> requestHeaders = Server.requestHeaders(exchange);
        assert request != null;
        HashMap<String,String> requestParams = Server.requestParams(request);
        String respText = response(request, requestHeaders, requestParams, clientIp);
        exchange.sendResponseHeaders(200, respText.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(respText.getBytes());
        output.flush();
        exchange.close();
    }

    String response(String request, HashMap<String,String> requestHeaders, HashMap<String,String> requestParams, String clientIp);
}
