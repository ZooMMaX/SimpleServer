package ru.zoommax;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.zoommax.next.Request;
import ru.zoommax.next.Response;
import ru.zoommax.next.ServerNext;
import ru.zoommax.next.annotation.Endpoint;
import ru.zoommax.next.annotation.InitWebServer;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.zoommax.next.annotation.documentation.CreateDocumentation;
import ru.zoommax.next.annotation.documentation.DocsGenerator;
import ru.zoommax.next.enums.HttpMethod;
import ru.zoommax.next.handlers.GetHandlerNew;
import ru.zoommax.next.handlers.PostHandlerNew;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * SimpleServer
 * @author ZooMMaX
 * @version 1.6
 * @since 11.01.24
 */
public class SimpleServer {

    public static Thread serverThread;

    public static final Logger logger = LoggerFactory.getLogger("SimpleServer");

    private static final Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage(""))
            .setScanners(Scanners.SubTypes, Scanners.ConstructorsAnnotated, Scanners.MethodsAnnotated, Scanners.FieldsAnnotated, Scanners.TypesAnnotated));

    static {
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(InitWebServer.class);
        for (Class<?> clazz : annotated) {
            if (clazz.isAnnotationPresent(InitWebServer.class)) {
                InitWebServer initWebServer = clazz.getAnnotation(InitWebServer.class);
                if (serverThread == null || !serverThread.isAlive()) {
                    serverThread = new Thread(ServerNext.getInstance(initWebServer.port(), initWebServer.threads()));
                    serverThread.start();
                }
            }
        }
        addEndpointsFromAnnotation();
        addDocs();
    }

    /**
     * Server initialization.<br>For start server use {@link #init(int port)}<br>
     * This method is deprecated in version 1.6. Use {@link #start()} instead.<br>
     * @param port port number on which the server will be running
     * */
    @Deprecated
    public static void init(int port){
        new Thread(new Server(port)).start();
        logger.info("Wait 2s before SimpleServer has started");
        try {
            Thread.sleep(2000);
            logger.info("SimpleServer is run");
        } catch (InterruptedException e) {
            logger.error("Error in SimpleServer.init", e);
        }
    }

    /**
     * Server initialization.<br>For start server use {@link #start()}<br>
     * This method definitely requires annotation {@link InitWebServer} for start server<br>
     * Method {@link #start()} will also initiate the search for annotated methods and add endpoints to the server.<br>
     * The invocation of endpoint handlers is done using reflection.
     * Example:<br>
     * Start server on default port (8080) with available processors - 1 threads:<br>
     * <pre>
     *     {@code
     *     {@literal @}InitWebServer
     *     public static void main(String[] args) {
     *         SimpleServer.start();
     *     }
     *     }
     * </pre>
     * Start server on custom port and custom threads:<br>
     * <pre>
     *     {@code
     *     {@literal @}InitWebServer(port = 25565, threads = 4)
     *     public static void main(String[] args) {
     *         SimpleServer.start();
     *     }
     *     }
     * </pre>
     * @see InitWebServer
     * @see Endpoint
     * */

    public static void start() {

        Set<Method> initWebServerAnnotated = reflections.getMethodsAnnotatedWith(InitWebServer.class);

        for (Method method : initWebServerAnnotated) {
            if (method.isAnnotationPresent(InitWebServer.class)) {
                InitWebServer initWebServer = method.getAnnotation(InitWebServer.class);
                if (serverThread == null || !serverThread.isAlive()) {
                    serverThread = new Thread(ServerNext.getInstance(initWebServer.port(),
                            initWebServer.threads() == 0 ? Runtime.getRuntime().availableProcessors() - 1 : initWebServer.threads()));
                    serverThread.start();
                }
            }
        }
        addEndpointsFromAnnotation();
        addDocs();
    }

    private static void addEndpointsFromAnnotation(){
        Set<Method> addEndpointAnnotated = reflections.getMethodsAnnotatedWith(Endpoint.class);

        for (Method method : addEndpointAnnotated) {
            if (method.isAnnotationPresent(Endpoint.class)) {
                Endpoint endpoint = method.getAnnotation(Endpoint.class);
                ServerNext serverNext = ServerNext.getInstance();

                HttpMethod httpMethod = endpoint.httpMethod();

                HttpHandler handler = null;
                switch (httpMethod) {
                    case GET:
                        handler = getHandler(method);
                        break;
                    case POST:
                        handler = postHandler(method);
                        break;
                    default:
                        logger.error("Unknown http method");
                        return;
                }

                serverNext.addEndpoint(endpoint.path(), handler);
            }
        }
    }

    private static void addDocs(){
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(CreateDocumentation.class);
        for (Class<?> clazz : annotated) {
            if (clazz.isAnnotationPresent(CreateDocumentation.class)) {
                CreateDocumentation createDocumentation = clazz.getAnnotation(CreateDocumentation.class);
                if (createDocumentation.value()) {
                    DocsGenerator.getInstance().generateDocsMethod(clazz);
                }
            }
        }
        annotated = reflections.getTypesAnnotatedWith(InitWebServer.class);
        for (Class<?> clazz : annotated) {
            if (clazz.isAnnotationPresent(InitWebServer.class) && clazz.isAnnotationPresent(CreateDocumentation.class)) {
                CreateDocumentation createDocumentation = clazz.getAnnotation(CreateDocumentation.class);
                if (createDocumentation.value()) {
                    DocsGenerator.getInstance().generateDocs(clazz);
                    break;
                }
            }
        }
    }

    /**
     * Server initialization.<br>For start server use {@link #start(int port, int threads)}<br>
     * Example:<br>
     * Start server on custom port and custom threads:<br>
     * <pre>
     *     {@code
     *     public static void main(String[] args) {
     *         SimpleServer.start(25565, 4);
     *     }
     *     }
     * </pre>
     * To add endpoints to the server, you must use the {@link #addEndpoint(String, HttpHandler)}<br>
     * Example:<br>
     * <pre>
     *     {@code
     *     public static void main(String[] args) {
     *         SimpleServer.start(25565, 4);
     *
     *         HttpHandler handler = new GetHandlerNew() {
     *             {@literal @}Override
     *             public Response response(String request, HashMap&lt;String, String&gt; requestHeaders, HashMap&lt;String, String&gt; requestParams, String clientIp) {
     *                 return Response.builder()
     *                   .bodyAsString("Hello world")
     *                   .statusCode(200)
     *                   .build();
     *             }
     *
     *         }
     *         SimpleServer.addEndpoint("/test", handler);
     *     }
     *     }
     * </pre>
     * @param port port number on which the server will be running
     * @param threads number of threads for the server
     * */
    public static void start(int port, int threads) {
        if (serverThread == null || !serverThread.isAlive()) {
            serverThread = new Thread(ServerNext.getInstance(port, threads));
            serverThread.start();
        }
    }

    /**
     * GetHandler for GET method
     * @param method with annotation {@link Endpoint}
     * @return {@link HttpHandler}
     */
    private static HttpHandler getHandler(Method method) {
        return new GetHandlerNew() {
            @Override
            public Response response(String request, HashMap<String, String> requestHeaders, HashMap<String, String> requestParams, String clientIp) {
                if (requestHeaders.containsKey("content-length") && method.getAnnotation(Endpoint.class).filterContentLength() > -1) {
                    int contentLength = Integer.parseInt(requestHeaders.get("content-length"));
                    if (contentLength > method.getAnnotation(Endpoint.class).filterContentLength()) {
                        return Response.builder()
                                .bodyAsString("413 Request Entity Too Large")
                                .statusCode(413)
                                .build();
                    }
                } else if (!requestHeaders.containsKey("content-length") && method.getAnnotation(Endpoint.class).filterContentLength() > -1) {
                    return Response.builder()
                            .bodyAsString("411 Length Required")
                            .statusCode(411)
                            .build();
                }
                try {
                    Object obj = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                    Request req = new Request();
                    req.setBodyAsString(request);
                    req.setHeaders(requestHeaders);
                    req.setParams(requestParams);
                    req.setClientIp(clientIp);
                    Response response = (Response) method.invoke(obj, req);
                    if (response.getStatusCode() == 0) {
                        response.setStatusCode(method.getAnnotation(Endpoint.class).statusCode());
                    }
                    return response;
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                    logger.error("Error in GetHandlerString.response", e);
                    return Response.builder()
                            .bodyAsString("500 Internal Server Error")
                            .statusCode(500)
                            .build();
                }
            }
        };
    }

    /**
     * PostHandler for POST method
     * @param method with annotation {@link Endpoint}
     * @return {@link HttpHandler}
     */
    private static HttpHandler postHandler(Method method) {
        return new PostHandlerNew() {
            @Override
            public Response response(InputStream requestBody, HashMap<String, String> requestHeaders, String clientIp) {
                if (requestHeaders.containsKey("content-length") && method.getAnnotation(Endpoint.class).filterContentLength() > -1) {
                    int contentLength = Integer.parseInt(requestHeaders.get("content-length"));
                    if (contentLength > method.getAnnotation(Endpoint.class).filterContentLength()) {
                        return Response.builder()
                                .bodyAsString("413 Request Entity Too Large")
                                .statusCode(413)
                                .build();
                    }
                }else if (!requestHeaders.containsKey("content-length") && method.getAnnotation(Endpoint.class).filterContentLength() > -1) {
                    return Response.builder()
                            .bodyAsString("411 Length Required")
                            .statusCode(411)
                            .build();
                }
                try {
                    Object obj = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                    Request req = new Request();
                    req.setBodyAsStream(requestBody);
                    req.setBodyAsBytes(requestBody.readAllBytes());
                    req.setBodyAsString(new String(requestBody.readAllBytes(), StandardCharsets.UTF_8));
                    req.setHeaders(requestHeaders);
                    req.setClientIp(clientIp);
                    Response response = (Response) method.invoke(obj, req);
                    if (response.getStatusCode() == 0) {
                        response.setStatusCode(method.getAnnotation(Endpoint.class).statusCode());
                    }
                    return response;
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException | IOException e) {
                    logger.error("Error in PostHandlerByteArray.response", e);
                    return Response.builder()
                            .bodyAsString("500 Internal Server Error")
                            .statusCode(500)
                            .build();
                }
            }
        };
    }

    /**
     * Add endpoint to server<br>
     * Example:<br>
     * <pre>
     *     {@code
     *     public static void main(String[] args) {
     *          SimpleServer.start(25565, 4);
     *          HttpHandler handler = new GetHandlerNew() {
     *              {@literal @}}Override
     *              public Response response(String request, HashMap&lt;String, String&gt; requestHeaders, HashMap&lt;String, String&gt; requestParams, String clientIp) {
     *                  return Response.builder()
     *                      .bodyAsString("Hello world")
     *                      .statusCode(200)
     *                      .build();
     *              }
     *          }
     *          SimpleServer.addEndpoint("/test", handler);
     *     }
     * </pre>
     * @param endPointName endpoint name
     * @param handler {@link HttpHandler}
     * @see Endpoint
     */
    public static void addEndpoint(String endPointName, HttpHandler handler) {
        ServerNext serverNext = ServerNext.getInstance();
        serverNext.addEndpoint(endPointName, handler);
    }

    /**
     * Decode url. Used in {@link GetHandlerNew} and {@link PostHandlerNew} interfaces.
     * @param encoded {@link String} encoded url
     * @return {@link String} decoded url
     */
    public static String decode(final String encoded) {
        return encoded == null ? null : URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    /**
     * Get request headers. Used in {@link GetHandlerNew} and {@link PostHandlerNew} interfaces.
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
        return headers;
    }

    /**
     * Get request params. Used in {@link GetHandlerNew} and {@link PostHandlerNew} interfaces.
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
     * Get client ip. Used in {@link GetHandlerNew} and {@link PostHandlerNew} interfaces.
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