package ru.zoommax.next.annotation;


import ru.zoommax.next.Request;
import ru.zoommax.next.Response;
import ru.zoommax.next.enums.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Endpoint annotation for methods.<br>
 * <br>
 * <b>Example:</b><br>
 * <pre>
 *     {@code
 *     @Endpoint(path = "/test", httpMethod = HttpMethod.GET, statusCode = 200, filterContentLength = -1)
 *     public Response test(Request request){
 *          return Response.builder()
 *              .bodyAsString(request.getBodyAsString())
 *              .statusCode(200)
 *              .build();
 *     }
 *     }
 * </pre>
 * @see InitWebServer
 * @see Request
 * @see Response
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {
    /**
     * @return {@link String} path of endpoint
     */
    String path();
    /**
     * @return {@link Integer} status code of endpoint. Default is 200
     */
    int statusCode() default 200;
    /**
     * @return {@link HttpMethod} http method of endpoint. Default is GET
     */
    HttpMethod httpMethod() default HttpMethod.GET;
    /**
     * @return {@link Integer} for filter content length. Default is -1 (not filter)
     */
    int filterContentLength() default -1;
}
