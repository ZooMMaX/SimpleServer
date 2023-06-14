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

public class Server implements Runnable{

    private static HttpServer server;
    private int port;
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

    public static void endPoint(String endpoint, HttpHandler handler){
        server.createContext(endpoint, handler);
        System.out.println("add "+endpoint);
    }

    public static void remEndPoint(String endpoint){
        server.removeContext(endpoint);
        System.out.println("remove "+endpoint);
    }

    public static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }

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
}
