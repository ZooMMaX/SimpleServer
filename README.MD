# Welcome to SimpleServer Library!

![Maven Central](https://img.shields.io/maven-central/v/ru.zoommax/SimpleServer?style=plastic)
![GitHub](https://img.shields.io/github/license/ZooMMaX/SimpleServer?style=plastic)
[![GitHub issues](https://img.shields.io/github/issues/ZooMMaX/SimpleServer?style=plastic)](https://github.com/ZooMMaX/SimpleServer/issues)

SimpleServer is a lightweight Java library that allows for quick and easy usage of an embedded web server. It provides a seamless way to add and remove endpoints on the fly, while offering fast access to request bodies, headers, and parameters. With SimpleServer, you can effortlessly handle POST and GET requests and send responses.

The goal of this library is to be simple and lightweight.

It quickly integrates into solutions and does not depend on programming patterns.

It's convenient!

## Key Features and Benefits

- **Fast and Easy**: SimpleServer simplifies the process of writing small APIs by providing a lightweight solution.
- **Flexible Endpoint Management**: Add or remove endpoints dynamically, allowing for easy customization of your server.
- **Efficient Request Handling**: Access request bodies, headers, and parameters quickly and effortlessly.
- **Convenient POST and GET Implementation**: SimpleServer offers interfaces for fast implementation of POST and GET requests.
- **Returns Responses**: Get the desired responses from your server effortlessly.
- **Annotation-based Implementation**: SimpleServer supports annotation-based implementation for easy and quick setup of endpoints.
- **Documentation Generation**: SimpleServer can generate documentation for your endpoints in the MarkDown format.

## Usage

To get started with SimpleServer, you need to explicitly start the server and then pass the endpoints to it.

Here's an example of how to use SimpleServer (Deprecated):

```java
// Import libraries

import ru.zoommax.EndPoint;
import ru.zoommax.GetHandler;
import ru.zoommax.PostHandler;
import ru.zoommax.SimpleServer;

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

Here's an example of how to use SimpleServer (New):

Annotation-based implementation

```java
//Import libraries
import ru.zoommax.SimpleServer;
import ru.zoommax.next.Request;
import ru.zoommax.next.Response;
import ru.zoommax.next.annotation.Endpoint;
import ru.zoommax.next.annotation.InitWebServer;
import ru.zoommax.next.enums.HttpMethod;

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
```

Annotation-based implementation with extends

```java
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
```

Not-annotation-based implementation

```java
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
```

# Generated documentation for the endpoint

The documentation is generated in the MarkDown format. All documentation is described in the form of annotations. The title page of the documentation is available at `http(s)://localhost:port/docs`.

Example of generated documentation for the endpoint:

```java
@InitWebServer(port = 25565, titleHomePage = "Documentation for WalletAPI",
        descriptionHomePage = "This is a documentation of WalletAPI. You can see all available endpoints and their descriptions.")
@CreateDocumentation(value = true)
public class API extends SimpleServer {
    private static CryptoWallet wallet;
    private static final Logger logger = LoggerFactory.getLogger(API.class);

    public void start(CryptoWallet wallet) {
        API.wallet = wallet;
        SimpleServer.start();
    }

    @Endpoint(path = "/api/v1/createInvoice", httpMethod = HttpMethod.GET)
    @ApiVersion("1.0")
    @PropertyDoc(name = {"newAddress", "amount", "address"},
            description = {"Create new address", "Amount", "Address (required if newAddress is false)"},
            type = {"Boolean", "Double", "String"},
            required = {true, true, false})
    @RequestDoc("Create invoice")
    @ResponseDoc(code = {200}, description = "Invoice created. Returns json with invoice info\n" +
            "\n" +
            "Example: \n" +
            "```json\n" +
            "{\n" +
            "   \"UID\": \"123e4567-e89b-12d3-a456-426614174000-1622542800000\",\n" +
            "   \"address\": \"1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa\",\n" +
            "   \"amount\": 0.01,\n" +
            "   \"createDate\": 1622542800000,\n" +
            "   \"expirationDate\": 1622546400000,\n" +
            "   \"paidDate\": 0,\n" +
            "   \"status\": \"WAITING\"\n" +
            "}\n" +
            "```\n" +
            "UID - Unique ID of invoice\n" +
            "\n" +
            "address - Address to send payment\n" +
            "\n" +
            "amount - Amount of invoice\n" +
            "\n" +
            "createDate - Date of creation\n" +
            "\n" +
            "expirationDate - Date of expiration\n" +
            "\n" +
            "paidDate - Date of payment\n" +
            "\n" +
            "status - Status of invoice\n" +
            "\n" +
            "Statuses: WAITING, PAID, EXPIRED, NULL")
    public static Response createInvoice(Request request) {
        logger.debug(request.getParams() + "");
        InvoicePojo invoicePojo = new InvoicePojo();
        String body = "";
        boolean newAddress = request.getParams().get("newAddress") != null && request.getParams().get("newAddress").equals("true");
        logger.debug("New address: " + newAddress);
        String address = "";
        if (newAddress) {
            address = wallet.getReceiveAddress();
        } else {
            address = request.getParams().get("address");
        }
        logger.debug("Address: " + address);
        double amount = request.getParams().get("amount") != null ? Double.parseDouble(request.getParams().get("amount")) : 0;
        logger.debug("Amount: " + amount);
        if (amount > 0) {
            invoicePojo.setUid(UUID.randomUUID() + "-" + System.currentTimeMillis());
            invoicePojo.setAmount(amount);
            invoicePojo.setAddress(address);
            invoicePojo.setCreateDate(System.currentTimeMillis());
            invoicePojo.setExpirationDate(System.currentTimeMillis() + 3600000);
            invoicePojo.setStatus(InvoiceStatus.WAITING);
            if (invoicePojo.insert()) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    body = objectMapper.writeValueAsString(invoicePojo);
                } catch (Exception e) {
                    logger.error("Error creating payment", e);
                }
            }
        }
        return Response.builder()
                .bodyAsString(body)
                .statusCode(200)
                .build();
    }
}
```

### MD result Example:

---

[Back to home](/docs)

/api/v1/createInvoice
=====================

Method: `GET`
-------------

### Properties

| Name         | Type      | Description                               | Required |
| ------------ | --------- | ----------------------------------------- | -------- |
| `newAddress` | `Boolean` | Create new address                        | `true`   |
| `amount`     | `Double`  | Amount                                    | `true`   |
| `address`    | `String`  | Address (required if newAddress is false) | `false`  |

### Request

Create invoice

### Response

`200`

Invoice created. Returns json with invoice info

Example:
```json
{
   "UID": "123e4567-e89b-12d3-a456-426614174000-1622542800000",
   "address": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
   "amount": 0.01,
   "createDate": 1622542800000,
   "expirationDate": 1622546400000,
   "paidDate": 0,
   "status": "WAITING"
}
```
UID - Unique ID of invoice

address - Address to send payment

amount - Amount of invoice

createDate - Date of creation

expirationDate - Date of expiration

paidDate - Date of payment

status - Status of invoice

Statuses: WAITING, PAID, EXPIRED, NULL


---

## Dependency

![dependency maven](https://img.shields.io/badge/DEPENDENCY-Maven-C71A36?style=plastic&logo=apachemaven)
```xml
<dependencies>
    <dependency>
        <groupId>ru.zoommax</groupId>
        <artifactId>SimpleServer</artifactId>
        <version>1.9.5</version>
    </dependency>
</dependencies>
```

![dependency gradle](https://img.shields.io/badge/DEPENDENCY-Gradle-02303A?style=plastic&logo=gradle)
```groovy
implementation 'ru.zoommax:SimpleServer:1.9.5'
```

That's it! You're now ready to build your small APIs using SimpleServer.

Please note that this is just a basic example, and you can explore more advanced features and configurations in the SimpleServer documentation.

I'am hope you find SimpleServer helpful for your web server needs. If you have any questions or need further assistance, please don't hesitate to open issue.

Happy coding!
