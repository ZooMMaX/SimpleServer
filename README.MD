```java
import ru.zoommax.*;

import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        SimpleServer.init(8080);

        EndPoint test = EndPoint.builder().handler(new GetHandler() {
            @Override
            public String response(String request) {
                return request;
            }
        }).endPointName("test").build();
        test.add();

        EndPoint test2 = EndPoint.builder().handler(new PostHandler() {
            @Override
            public String response(String requestBody) {
                HashMap<String, String> params = Server.requestParams(requestBody);
                return requestBody;
            }
        }).endPointName("test2").build();
        test2.add();
    }
}
```