package ru.zoommax;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class for creating http server in second thread,
 * adding and removing endpoints, getting request headers and params, getting client ip,
 * decoding url, sending response.
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
 */
public class Server implements Runnable{

    /**
     * HttpServer object
     * @see HttpServer
     */
    private static HttpServer server;

    /**
     * Port number on which the server will be running
     */
    private int port;

    /**
     * Server initialization.<br>For start server use {@link #run()}<br>
     * @param port port number on which the server will be running
     */
    public Server(int port){
        this.port = port;
    }
    public Server(){}

    @Override
    @SneakyThrows
    public void run() {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/alife", exchange -> {
            System.out.println("req");
            String request = Server.decode(exchange.getRequestURI().getRawQuery());
            System.out.println(request);
            String respText ="life";
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
            exchange.close();
        });

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        server.setExecutor(executor); // creates a default executor
        server.start();
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
    }

    /**
     * Add endpoint to server
     * @param endpoint {@link String} endpoint url. Example: "api/v1/test" <b>without root slash</b>
     * @param handler {@link HttpHandler} for endpoint. You can use {@link GetHandler} or {@link PostHandler} interfaces.
     */
    public static void endPoint(String endpoint, HttpHandler handler){
        server.createContext(endpoint, handler);
        System.out.println("add "+endpoint);
    }

    /**
     * Remove endpoint from server
     * @param endpoint {@link String} endpoint url. Example: "api/v1/test" <b>without root slash</b>
     */
    public static void remEndPoint(String endpoint){
        server.removeContext(endpoint);
        System.out.println("remove "+endpoint);
    }

    /**
     * Decode url. Used in {@link GetHandler} and {@link PostHandler} interfaces.
     * @param encoded {@link String} encoded url
     * @return {@link String} decoded url
     */
    public static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }

    /**
     * Get request headers. Used in {@link GetHandler} and {@link PostHandler} interfaces.
     * @param httpExchange {@link HttpExchange} object
     * @return {@link HashMap} with headers
     */
    public static HashMap<String, String> requestHeaders(HttpExchange httpExchange){
        Headers exchangeHeaders = httpExchange.getRequestHeaders();
        HashMap<String, String> headers = new HashMap<>();
        for(String key: exchangeHeaders.keySet()) {
            key = key.toLowerCase();
            headers.put(key, exchangeHeaders.getFirst(key));
        }
        System.out.println(headers);
        return headers;
    }

    /**
     * Get request params. Used in {@link GetHandler} and {@link PostHandler} interfaces.
     * @param paramsStr {@link String} params string. Example: "param1=value1{@literal &}param2=value2"
     * @return {@link HashMap} with params
     */
    public static HashMap<String, String> requestParams(String paramsStr){
        HashMap<String, String> params = new HashMap<>();
        for(String param: paramsStr.split("&")){
            String[] pair = param.split("=");
            params.put(pair[0], pair[1]);
        }
        return params;
    }

    /**
     * Get client ip. Used in {@link GetHandler} and {@link PostHandler} interfaces.
     * @param httpExchange {@link HttpExchange} object
     * @return {@link String} client ip
     */
    public static String getIp(HttpExchange httpExchange){
        HashMap<String,String> headers = requestHeaders(httpExchange);
        if(headers.containsKey("x-forwarded-for"))
            return headers.get("x-forwarded-for");
        else {
            return httpExchange.getRemoteAddress().getAddress().getHostAddress();
        }
    }
}
