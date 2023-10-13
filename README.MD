# Welcome to SimpleServer Library!

SimpleServer is a lightweight Java library that allows for quick and easy usage of an embedded web server. It provides a seamless way to add and remove endpoints on the fly, while offering fast access to request bodies, headers, and parameters. With SimpleServer, you can effortlessly handle POST and GET requests and send responses.

## Key Features and Benefits

- **Fast and Easy**: SimpleServer simplifies the process of writing small APIs by providing a lightweight solution.
- **Flexible Endpoint Management**: Add or remove endpoints dynamically, allowing for easy customization of your server.
- **Efficient Request Handling**: Access request bodies, headers, and parameters quickly and effortlessly.
- **Convenient POST and GET Implementation**: SimpleServer offers interfaces for fast implementation of POST and GET requests.
- **Returns Responses**: Get the desired responses from your server effortlessly.

## Usage

To get started with SimpleServer, you need to explicitly start the server and then pass the endpoints to it.

Here's an example of how to use SimpleServer:

```java
// Import libraries
import ru.zoommax.*;
import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        // Initialize the server by specifying the port it will listen on
        SimpleServer.init(8080);

        // Create an endpoint that handles GET requests
        // Here's an example of an endpoint that simply returns the request it received
        EndPoint test = EndPoint.builder().handler(new GetHandler() {
            @Override
            public String response(String request, HashMap<String, String> requestHeaders, HashMap<String, String> requestParams, String clientIp) {
                // You can access request headers, request parameters, and client IP address here
                // Customize the response based on the request if needed
                return request;
            }
        }).endPointName("test").build();

        // Add the created endpoint to the server
        test.add();

        // Create another endpoint that handles POST requests
        // Here's an example of an endpoint that simply returns the request body it received
        EndPoint test2 = EndPoint.builder().handler(new PostHandler() {
            @Override
            public String response(String requestBody, HashMap<String, String> requestHeaders, String clientIp) {
                // You can access request headers, request body, and client IP address here
                // Customize the response based on the request if needed
                return requestBody;
            }
        }).endPointName("test2").build();

        // Add the second endpoint to the server
        test2.add();
    }
}
// Additionally, you can pass an HttpHandler to the endpoint builder. GetHandler and PostHandler inherit from HttpHandler.
```

That's it! You're now ready to build your small APIs using SimpleServer.

Please note that this is just a basic example, and you can explore more advanced features and configurations in the SimpleServer documentation.

We hope you find SimpleServer helpful for your web server needs. If you have any questions or need further assistance, please don't hesitate to reach out.

Happy coding!
