package ru.zoommax;


import com.sun.net.httpserver.HttpHandler;
import lombok.Builder;

/**
 * Class for build (use builder pattern) and add or remove endpoints.
 * @see EndPoint#add()
 * @see EndPoint#remove()
 * @author ZooMMaX
 * @version 1.3
 * @since 13.10.23
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
    public void add(){
        Server.endPoint("/"+endPointName, handler);
    }

    /**
     * Remove endpoint from server
     */
    public void remove(){
        Server.remEndPoint(endPointName);
    }
}
