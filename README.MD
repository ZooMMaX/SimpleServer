```java
import ru.zoommax.EndPoint;
import ru.zoommax.SimpleServer;
import ru.zoommax.StdHandler;

public class Test {
    public static void main(String[] args) {
        SimpleServer.init(8080);

        EndPoint test = EndPoint.builder().handler(new StdHandler() {
            @Override
            public String response(String request) {
                return request;
            }
        }).endPointName("test").build();
        test.add();
    }
}
```