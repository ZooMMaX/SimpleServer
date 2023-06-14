package ru.zoommax;


import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;

@Builder
public class EndPoint {
    private HttpHandler handler;
    private String endPointName;

    public void add(){
        Server.endPoint("/"+endPointName, handler);
    }

    public void remove(){
        Server.remEndPoint(endPointName);
    }
}
