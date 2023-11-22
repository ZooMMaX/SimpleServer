package ru.zoommax;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.HashMap;

/**
 * Interface for creating <b>POST</b> method endpoints.
 * @see PostHandler#response(String, HashMap, String)
 * @see PostHandler#handle(HttpExchange)
 * @see Server#endPoint(String, HttpHandler)
 * @see Server#remEndPoint(String)
 * @see Server#requestHeaders(HttpExchange)
 * @see Server#getIp(HttpExchange)
 * @see Server#run()
 * @see Server#Server(int)
 * @version 1.3
 * @since 13.10.23
 * */
public interface PostHandler extends HttpHandler {
    /**
     * Method for creating <b>POST</b> method endpoints.<br>
     * This method is called when a request is received.<br>
     * In this method automatically get request headers, request body and client ip.<br>
     * @see PostHandler#response(String, HashMap, String)
     * @param exchange {@link HttpExchange} object
     * */
    @Override
    default void handle(HttpExchange exchange) throws IOException {
        String respText = "only post";
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")){
            String clientIp = Server.getIp(exchange);
            InputStream is = exchange.getRequestBody();
            StringBuilder stringBuilder = new StringBuilder();

            new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .forEach( (String s) -> stringBuilder.append(s));

            respText = response(stringBuilder.toString(), Server.requestHeaders(exchange), clientIp);
        }
        exchange.sendResponseHeaders(200, respText.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(respText.getBytes());
        output.flush();
        exchange.close();
    }

    /**
     * Called when a request is received.<br>
     * @see PostHandler#handle(HttpExchange)
     * @param requestBody {@link String} request body
     * @param requestHeaders {@link HashMap} request headers
     * @param clientIp {@link String} client ip
     * @return {@link String} response text
     */
    String response(String requestBody, HashMap<String,String> requestHeaders, String clientIp);
}
