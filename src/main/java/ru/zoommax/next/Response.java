package ru.zoommax.next;

import com.sun.net.httpserver.Headers;
import lombok.Builder;
import lombok.Data;

/**
 * Response class<br>
 * Use for set and get response data<br>
 * <br>
 * <b>Fields:</b><br>
 * <ul>
 *     <li>{@link #bodyAsString}</li>
 *     <li>{@link #bodyAsBytes}</li>
 *     <li>{@link #statusCode}</li>
 *     <li>{@link #headers}</li>
 * </ul>
 * @since 11.01.24
 * @version 1.6
 * @author ZooMMaX
 * @see Request
 */
@Data
@Builder
public class Response {
    private byte[] bodyAsBytes;
    private String bodyAsString;
    private int statusCode;
    private Headers headers;
}
