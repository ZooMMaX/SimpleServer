package ru.zoommax;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Iterator;

/**
 * SimpleServer
 * @author ZooMMaX
 * @version 1.3
 * @since 13.10.23
 */
public class SimpleServer {
    /**
     * Server initialization.<br>For start server use {@link #init(int port)}<br>
     * @param port port number on which the server will be running
     * */
    public static void init(int port){
        Logger logger = LoggerFactory.getLogger(SimpleServer.class);
        new Thread(new Server(port)).start();
        logger.info("Wait 2s before SimpleServer has started");
        try {
            Thread.sleep(2000);
            logger.info("SimpleServer is run");
        } catch (InterruptedException e) {
            logger.error("Error in SimpleServer.init", e);
        }
    }
}