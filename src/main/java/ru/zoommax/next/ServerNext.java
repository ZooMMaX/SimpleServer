package ru.zoommax.next;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import ru.zoommax.SimpleServer;
import ru.zoommax.next.annotation.documentation.DocsGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ru.zoommax.SimpleServer.logger;

@Getter
public class ServerNext implements Runnable{
    private static ServerNext instance;
    private static HttpServer serverHttp;
    private ExecutorService serverExecutor;
    private final int port;
    private final int threads;

    private ServerNext(int port, int threads) {
        this.port = port;
        this.threads = threads;
    }

    public static ServerNext getInstance() {
        if (instance == null) {
            instance = new ServerNext(8080, Runtime.getRuntime().availableProcessors() -1);
        }
        return instance;
    }

    public static ServerNext getInstance(int port, int threads) {
        if (instance == null) {
            instance = new ServerNext(port, threads);
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            serverExecutor = Executors.newFixedThreadPool(threads);
            serverHttp = HttpServer.create();
            serverHttp.bind(new InetSocketAddress(port), 0);
            serverHttp.setExecutor(serverExecutor);
            serverHttp.createContext("/alife", exchange -> {
                logger.info("request to /alife");
                String request = SimpleServer.decode(exchange.getRequestURI().getRawQuery());
                logger.info("request: "+request);
                String respText = "life";
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                exchange.close();
            });
            serverHttp.start();
            serverExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (IOException e) {
            logger.error("Error in ServerHttp.ServerHttp", e);
            return;
        } catch (InterruptedException e) {
            logger.error("Error in ServerHttp.run", e);
            return;
        }
        logger.info("Server is run");
    }

    public void addEndpoint(String endPointName, HttpHandler handler) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() -1);
        executor.submit(() -> {
            logger.info("Add endpoint "+endPointName);
            while (serverHttp == null) {
                try {
                    logger.info("ServerHttp is not ready");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.error("Error in ServerHttp.addEndpoint", e);
                }
            }
            logger.info("ServerHttp is ready");
            serverHttp.createContext(endPointName, handler);
            logger.info("Endpoint "+endPointName+" is added");
        });
    }
}
