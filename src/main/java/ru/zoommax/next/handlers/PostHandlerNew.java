package ru.zoommax.next.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.zoommax.PostHandler;
import ru.zoommax.Server;
import ru.zoommax.SimpleServer;
import ru.zoommax.next.Response;

import java.io.*;
import java.util.HashMap;

import static ru.zoommax.SimpleServer.logger;

/**
 * Interface for creating <b>POST</b> method endpoints.
 * @see PostHandlerNew#response(InputStream, HashMap, String)
 * @see PostHandlerNew#handle(HttpExchange)
 * @see SimpleServer#addEndpoint(String, HttpHandler)
 * @see SimpleServer#requestHeaders(HttpExchange)
 * @see SimpleServer#getIp(HttpExchange)
 * @version 1.6
 * @since 11.01.24
 * @author ZooMMaX
 * */
public interface PostHandlerNew extends HttpHandler {

    /**
     * Method for creating <b>POST</b> method endpoints.<br>
     * This method is called when a request is received.<br>
     * In this method automatically get request headers, request body and client ip.<br>
     * @see PostHandlerNew#response(InputStream, HashMap, String)
     * @param exchange {@link HttpExchange} object
     * */
    @Override
    default void handle(HttpExchange exchange) {
        Response resp = Response.builder().bodyAsString("only post").statusCode(200).build();
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")){
            String clientIp = SimpleServer.getIp(exchange);
            InputStream is = exchange.getRequestBody();

            resp = response(is, SimpleServer.requestHeaders(exchange), clientIp);
        }
        byte[] respText = resp.getBodyAsBytes();
        if (respText == null) {
            respText = resp.getBodyAsString().getBytes();
        }
        int respCode = resp.getStatusCode();
        Headers headers = resp.getHeaders();
        if (headers == null) {
            headers = new Headers();
        }
        try {
            exchange.sendResponseHeaders(respCode, respText.length);
            exchange.getResponseHeaders().putAll(headers);
            OutputStream output = exchange.getResponseBody();
            output.write(respText);
            output.flush();
            exchange.close();
        } catch (IOException e) {
            logger.error("Error in PostHandler.handle", e);
        }
    }

    /**
     * Called when a request is received.<br>
     * @see PostHandlerNew#handle(HttpExchange)
     * @param requestBody {@link String} request body
     * @param requestHeaders {@link HashMap} request headers
     * @param clientIp {@link String} client ip
     * @return {@link String} response text
     */
    Response response(InputStream requestBody, HashMap<String,String> requestHeaders, String clientIp);
}
