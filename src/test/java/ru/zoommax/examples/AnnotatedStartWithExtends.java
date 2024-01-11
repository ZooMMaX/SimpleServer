package ru.zoommax.examples;

import ru.zoommax.SimpleServer;
import ru.zoommax.next.Request;
import ru.zoommax.next.Response;
import ru.zoommax.next.annotation.Endpoint;
import ru.zoommax.next.annotation.InitWebServer;
import ru.zoommax.next.enums.HttpMethod;

@InitWebServer(port = 12345, threads = 4)
public class AnnotatedStartWithExtends extends SimpleServer {
    public static void main(String[] args) {

    }

    @Endpoint(path = "/test", httpMethod = HttpMethod.GET, statusCode = 200, filterContentLength = -1)
    public Response test(Request request) {
        return Response.builder()
                .bodyAsString(request.getBodyAsString())
                .statusCode(200)
                .build();
    }
}
