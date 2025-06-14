package space.zoommax.examples;

import space.zoommax.SimpleServer;
import space.zoommax.next.Request;
import space.zoommax.next.Response;
import space.zoommax.next.annotation.Endpoint;
import space.zoommax.next.annotation.InitWebServer;
import space.zoommax.next.enums.HttpMethod;

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
