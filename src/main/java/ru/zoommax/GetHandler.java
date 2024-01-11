package ru.zoommax;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import static ru.zoommax.SimpleServer.logger;

/**
 * Interface for creating <b>GET</b> method endpoints.
 * @see GetHandler#response(String, HashMap, HashMap, String)
 * @see GetHandler#handle(HttpExchange)
 * @see Server#endPoint(String, HttpHandler)
 * @see Server#remEndPoint(String)
 * @see Server#requestHeaders(HttpExchange)
 * @see Server#requestParams(String)
 * @see Server#getIp(HttpExchange)
 * @see Server#decode(String)
 * @see Server#run()
 * @see Server#Server(int)
 * @author ZooMMaX
 * @version 1.3
 * @since 13.10.23
 * */
@Deprecated
public interface GetHandler extends HttpHandler {
    /**
     * Method for creating <b>GET</b> method endpoints.<br>
     * This method is called when a request is received.<br>
     * In this method automatically decoded url, request headers and request params.<br>
     * @see GetHandler#response(String, HashMap, HashMap, String)
     * @param exchange {@link HttpExchange} object
     * */
    @Override
    default void handle(HttpExchange exchange) {
        String clientIp = Server.getIp(exchange);
        String request = Server.decode(exchange.getRequestURI().getRawQuery());
        HashMap<String,String> requestHeaders = Server.requestHeaders(exchange);
        HashMap<String,String> requestParams = new HashMap<>();
        if (request != null && !request.equals("")){
            requestParams = Server.requestParams(request);
        }
        String respText = response(request, requestHeaders, requestParams, clientIp);
        try {
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
            exchange.close();
        } catch (IOException e) {
            logger.error("Error in GetHandler.handle", e);
        }
    }

    /**
     * Called when a request is received.<br>
     * @see GetHandler#handle(HttpExchange)
     * @param request {@link String} decoded url
     * @param requestHeaders {@link HashMap} request headers
     * @param requestParams {@link HashMap} request params
     * @param clientIp {@link String} client ip
     * @return {@link String} response text
     */
    String response(String request, HashMap<String,String> requestHeaders, HashMap<String,String> requestParams, String clientIp);
}
