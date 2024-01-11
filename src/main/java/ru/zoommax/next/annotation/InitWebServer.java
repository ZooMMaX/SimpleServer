package ru.zoommax.next.annotation;

import ru.zoommax.SimpleServer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * InitWebServer annotation for methods.<br>
 * <br>
 Start server on default port (8080) with available processors - 1 threads:<br>
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
 * @see Endpoint
 * @see SimpleServer#start()
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InitWebServer {
    /**
     * @return {@link Integer} port of server. Default is 8080
     */
    int port() default 8080;
    /**
     * @return {@link Integer} threads of server. Default - 1
     */
    int threads() default 1;
}
