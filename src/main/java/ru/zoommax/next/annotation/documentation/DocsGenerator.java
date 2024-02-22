package ru.zoommax.next.annotation.documentation;

import net.steppschuh.markdowngenerator.link.Link;
import net.steppschuh.markdowngenerator.list.UnorderedList;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.code.Code;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import ru.zoommax.next.Response;
import ru.zoommax.next.ServerNext;
import ru.zoommax.next.annotation.Endpoint;
import ru.zoommax.next.annotation.InitWebServer;
import ru.zoommax.next.handlers.GetHandlerNew;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DocsGenerator {
    private static DocsGenerator instance;
    String homepage;
    HashMap<String, String> endpointsPages;
    HashMap<String, String> endpointsLinks;

    private DocsGenerator() {
        endpointsPages = new HashMap<>();
        endpointsLinks = new HashMap<>();
        homepage = "";
    }

    public static synchronized DocsGenerator getInstance() {
        if (instance == null) {
            instance = new DocsGenerator();
        }
        return instance;
    }

    public void generateDocs(Class<?> clazz) {
        StringBuilder homepageSB = new StringBuilder();
        if (clazz.isAnnotationPresent(InitWebServer.class)) {
            InitWebServer annotation = clazz.getAnnotation(InitWebServer.class);
            homepageSB.append(new Heading(annotation.titleHomePage(), 1)).append("\n\n");
            homepageSB.append(new Heading(annotation.descriptionHomePage(), 3)).append("\n\n");
            homepageSB.append("Port: ").append(annotation.port()).append("\n\n");
            List<Link> links = new ArrayList<>();
            for (int x = 0; x < endpointsLinks.size(); x++) {
                String key = endpointsLinks.keySet().toArray()[x].toString();
                links.add(new Link(endpointsLinks.get(key)));
                addDocEndpoint(key);
            }
            homepageSB.append(new UnorderedList<>(links));
            homepage = homepageSB.toString();
        }


        ServerNext serverNext = ServerNext.getInstance();
        serverNext.addEndpoint("/docs", (GetHandlerNew) (request, requestHeaders, requestParams, clientIp) -> {
            String sb = "<script src=\"https://rawcdn.githack.com/oscarmorrison/md-page/232e97938de9f4d79f4110f6cfd637e186b63317/md-page.js\"></script><noscript>" + "\n\n" +
                    homepage + "\n\n";
            return Response.builder()
                    .bodyAsString(sb)
                    .statusCode(200)
                    .build();
        });
    }

    private void addDocEndpoint(String key) {
        ServerNext serverNext = ServerNext.getInstance();
        serverNext.addEndpoint(endpointsLinks.get(key), (GetHandlerNew) (request, requestHeaders, requestParams, clientIp) -> {
            String sb = "<script src=\"https://rawcdn.githack.com/oscarmorrison/md-page/232e97938de9f4d79f4110f6cfd637e186b63317/md-page.js\"></script><noscript>" + "\n\n" +
                    endpointsPages.get(key) + "\n\n";
            return Response.builder()
                    .bodyAsString(sb)
                    .statusCode(200)
                    .build();
        });
    }

    public void generateDocsMethod(Class<?> clazz){
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Endpoint.class)) {
                Endpoint annotation = method.getAnnotation(Endpoint.class);
                PropertyDoc propertyDoc = null;
                if (method.isAnnotationPresent(PropertyDoc.class)) {
                    propertyDoc = method.getAnnotation(PropertyDoc.class);
                }
                RequestDoc requestDoc = null;
                if (method.isAnnotationPresent(RequestDoc.class)) {
                    requestDoc = method.getAnnotation(RequestDoc.class);
                }
                ResponseDoc responseDoc = null;
                if (method.isAnnotationPresent(ResponseDoc.class)) {
                    responseDoc = method.getAnnotation(ResponseDoc.class);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(new Link("Back to home", "/docs")).append("\n\n");
                sb.append(new Heading(annotation.path(), 1)).append("\n\n");
                sb.append(new Heading("Method: " + new Code(annotation.httpMethod()), 2)).append("\n\n");
                if (propertyDoc != null) {
                    Table.Builder tableBuilder = new Table.Builder()
                            .addRow("Name","Type", "Description", "Required");
                    for (int i = 0; i < propertyDoc.name().length; i++) {
                        tableBuilder.addRow(new Code(propertyDoc.name()[i]), new Code(propertyDoc.type()[i]), propertyDoc.description()[i], new Code(propertyDoc.required()[i]));
                    }
                    sb.append(new Heading("Properties", 3)).append("\n\n");
                    sb.append(tableBuilder.build()).append("\n\n");
                }
                if (requestDoc != null) {
                    sb.append(new Heading("Request", 3)).append("\n\n");
                    sb.append(requestDoc.value()).append("\n\n");
                }
                if (responseDoc != null) {
                    sb.append(new Heading("Response", 3)).append("\n\n");
                    for (int i = 0; i < responseDoc.description().length; i++) {
                        sb.append(new Code(responseDoc.code()[i])).append("\n\n");
                        sb.append(responseDoc.description()[i]).append("\n\n");
                    }
                }

                if (method.isAnnotationPresent(ApiVersion.class)) {
                    ApiVersion apiVersion = method.getAnnotation(ApiVersion.class);
                    endpointsPages.put(apiVersion.value()+method.getName(), sb.toString());
                    endpointsLinks.put(apiVersion.value()+method.getName(), "/docs/api/v" + apiVersion.value() + "/" + method.getName());
                }else {
                    endpointsPages.put(method.getName(), sb.toString());
                    endpointsLinks.put(method.getName(), "/docs/api/" + method.getName());
                }
            }
        }
    }
}
