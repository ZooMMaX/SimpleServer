package ru.zoommax.next.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.zoommax.GetHandler;
import ru.zoommax.Server;
import ru.zoommax.SimpleServer;
import ru.zoommax.next.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import static ru.zoommax.SimpleServer.logger;

/**
 * Interface for creating <b>GET</b> method endpoints.
 * @see GetHandlerNew#response(String, HashMap, HashMap, String)
 * @see GetHandlerNew#handle(HttpExchange)
 * @see SimpleServer#addEndpoint(String, HttpHandler)
 * @see SimpleServer#requestHeaders(HttpExchange)
 * @see SimpleServer#requestParams(String)
 * @see SimpleServer#getIp(HttpExchange)
 * @see SimpleServer#decode(String)
 * @author ZooMMaX
 * @version 1.6
 * @since 11.01.24
 * */
public interface GetHandlerNew extends HttpHandler {

    /**
     * Method for creating <b>GET</b> method endpoints.<br>
     * This method is called when a request is received.<br>
     * In this method automatically decoded url, request headers and request params.<br>
     * @see GetHandlerNew#response(String, HashMap, HashMap, String)
     * @param exchange {@link HttpExchange} object
     * */
    @Override
    default void handle(HttpExchange exchange) {
        String clientIp = SimpleServer.getIp(exchange);
        String request = SimpleServer.decode(exchange.getRequestURI().getRawQuery());
        HashMap<String,String> requestHeaders = SimpleServer.requestHeaders(exchange);
        HashMap<String,String> requestParams = new HashMap<>();
        if (request != null && !request.isEmpty()){
            requestParams = SimpleServer.requestParams(request);
        }
        Response resp = response(request, requestHeaders, requestParams, clientIp);
        byte[] respBytes = resp.getBodyAsBytes();
        if (respBytes == null) {
            respBytes = resp.getBodyAsString().getBytes();
        }
        int respCode = resp.getStatusCode();
        Headers headers = resp.getHeaders();
        if (headers == null) {
            headers = new Headers();
        }
        exchange.getResponseHeaders().putAll(headers);
        try {
            exchange.sendResponseHeaders(respCode, respBytes.length);
            OutputStream output = exchange.getResponseBody();
            output.write(respBytes);
            output.flush();
            exchange.close();
        } catch (IOException e) {
            logger.error("Error in GetHandler.handle", e);
        }
    }

    /**
     * Called when a request is received.<br>
     * @see GetHandlerNew#handle(HttpExchange)
     * @param request {@link String} decoded url
     * @param requestHeaders {@link HashMap} request headers
     * @param requestParams {@link HashMap} request params
     * @param clientIp {@link String} client ip
     * @return {@link String} response text
     */
    Response response(String request, HashMap<String,String> requestHeaders, HashMap<String,String> requestParams, String clientIp);
}
