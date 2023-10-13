package ru.zoommax;

import lombok.SneakyThrows;

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
    @SneakyThrows
    public static void init(int port){
        new Thread(new Server(port)).start();
        System.out.println("Wait 2s before SimpleServer has started");
        Thread.sleep(2000);
        System.out.println("SimpleServer is run");
    }
}