package ru.zoommax;


import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;

/**
 * Class for build (use builder pattern) and add or remove endpoints.
 * @see EndPoint#add()
 * @see EndPoint#remove()
 * @see EndPoint#addNew()
 * @author ZooMMaX
 * @version 1.6
 * @since 11.01.24
 */
@Builder
public class EndPoint {
    /**
     * HttpHandler object
     * @see HttpHandler
     */
    private HttpHandler handler;
    /**
     * Endpoint url
     */
    private String endPointName;

    /**
     * Add endpoint to server
     */
    @Deprecated
    public void add(){
        Server.endPoint("/"+endPointName, handler);
    }

    /**
     * Remove endpoint from server
     */
    @Deprecated
    public void remove(){
        Server.remEndPoint(endPointName);
    }

    /**
     * Add endpoint to server
     */
    public void addNew() {
        SimpleServer.addEndpoint(endPointName, handler);
    }
}
