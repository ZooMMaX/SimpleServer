package ru.zoommax;

import lombok.SneakyThrows;

public class SimpleServer {
    @SneakyThrows
    public static void init(int port){
        new Thread(new Server(port)).start();
        System.out.println("Wait 2s before SimpleServer has started");
        Thread.sleep(2000);
        System.out.println("SimpleServer is run");
    }
}