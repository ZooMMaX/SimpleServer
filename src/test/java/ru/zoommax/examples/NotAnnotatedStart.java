package ru.zoommax.examples;

import ru.zoommax.SimpleServer;
import ru.zoommax.next.Response;
import ru.zoommax.next.handlers.GetHandlerNew;

import java.util.HashMap;

public class NotAnnotatedStart {
    //Initialization of server with port 12345 and 4 threads and start it
    public static void main(String[] args) {
        SimpleServer.start(12345, 4);
        test();
    }

    //Add endpoint using SimpleServer.addEndpoint()
    public static void test() {
        SimpleServer.addEndpoint("/test", new GetHandlerNew() {
            @Override
            public Response response(String request, HashMap<String, String> requestHeaders, HashMap<String, String> requestParams, String clientIp) {
                //Endpoint logic
                String body = request;
                //...
                return Response.builder()
                        .bodyAsString(body)
                        .statusCode(200)
                        .build();
            }
        });
    }
}
