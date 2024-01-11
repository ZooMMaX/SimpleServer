package ru.zoommax.next;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Request class<br>
 * Use for set and get request data<br>
 * <br>
 * <b>Fields:</b><br>
 * <ul>
 *     <li>{@link #bodyAsString}</li>
 *     <li>{@link #bodyAsBytes}</li>
 *     <li>{@link #bodyAsStream}</li>
 *     <li>{@link #headers}</li>
 *     <li>{@link #params}</li>
 *     <li>{@link #request}</li>
 *     <li>{@link #clientIp}</li>
 * </ul>
 * @since 11.01.24
 * @version 1.6
 * @author ZooMMaX
 * @see Response
 */
@Getter
@Setter
public class Request {
    private String bodyAsString;
    private byte[] bodyAsBytes;
    private InputStream bodyAsStream;
    private HashMap<String, String> headers;
    private HashMap<String, String> params;
    private String request;
    private String clientIp;
}
