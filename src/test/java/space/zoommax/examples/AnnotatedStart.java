package space.zoommax.examples;

import space.zoommax.SimpleServer;
import space.zoommax.next.Request;
import space.zoommax.next.Response;
import space.zoommax.next.annotation.Endpoint;
import space.zoommax.next.annotation.InitWebServer;
import space.zoommax.next.enums.HttpMethod;

public class AnnotatedStart {
    //Initialization of server with port 12345 and 4 threads using annotation InitWebServer
    @InitWebServer(port = 12345, threads = 4)
    public static void main(String[] args) {
        //Start server using SimpleServer.start()
        SimpleServer.start();
    }

    //Add endpoint using annotation Endpoint
    @Endpoint(path = "/test", httpMethod = HttpMethod.GET, statusCode = 200, filterContentLength = -1)
    public Response test(Request request) {
        //Endpoint logic
        String body = request.getBodyAsString();
        //...

        //Return response
        return Response.builder()
                .bodyAsString(body)
                .statusCode(200)
                .build();
    }
}
