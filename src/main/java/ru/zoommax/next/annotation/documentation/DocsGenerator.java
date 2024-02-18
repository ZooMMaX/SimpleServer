package ru.zoommax.next.annotation.documentation;

import javassist.Loader;
import net.steppschuh.markdowngenerator.link.Link;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.code.Code;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import ru.zoommax.next.Response;
import ru.zoommax.next.ServerNext;
import ru.zoommax.next.annotation.Endpoint;
import ru.zoommax.next.annotation.InitWebServer;
import ru.zoommax.next.handlers.GetHandlerNew;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DocsGenerator {
    String homepage = "";
    List<String> endpointsPages = new ArrayList<>();
    List<String> endpointsLinks = new ArrayList<>();
    private static final Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage(""))
            .setScanners(Scanners.SubTypes, Scanners.ConstructorsAnnotated, Scanners.MethodsAnnotated, Scanners.FieldsAnnotated, Scanners.TypesAnnotated));

    public void generateDocs() {
        Set<Method> addEndpointAnnotated = reflections.getMethodsAnnotatedWith(Endpoint.class);

        for (Method method : addEndpointAnnotated) {
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
                sb.append(new Heading(annotation.path(), 1)).append("\n");
                sb.append("Method: ").append(annotation.httpMethod()).append("\n\n");
                if (propertyDoc != null) {
                    Table.Builder tableBuilder = new Table.Builder()
                            .addRow("Name","Type", "Description", "Required");
                    for (int i = 0; i < propertyDoc.name().length; i++) {
                        tableBuilder.addRow(new Code(propertyDoc.name()[i]), new Code(propertyDoc.type()[i]), propertyDoc.description()[i], new Code(propertyDoc.required()[i]));
                    }
                    sb.append(new Heading("Properties", 2)).append("\n");
                    sb.append(tableBuilder.build()).append("\n");
                }
                if (requestDoc != null) {
                    sb.append(new Heading("Request", 2)).append("\n");
                    sb.append(requestDoc.value()).append("\n");
                }
                if (responseDoc != null) {
                    sb.append(new Heading("Response", 2)).append("\n");
                    for (int i = 0; i < responseDoc.description().length; i++) {
                        sb.append(new Code(responseDoc.code()[i])).append("\n");
                        sb.append(responseDoc.description()[i]).append("\n");
                    }
                }
                endpointsPages.add(sb.toString());
                if (method.isAnnotationPresent(ApiVersion.class)) {
                    ApiVersion apiVersion = method.getAnnotation(ApiVersion.class);
                    endpointsLinks.add("/docs/api/v" + apiVersion.value() + "/" + method.getName());
                }else {
                    endpointsLinks.add("/docs/api/" + method.getName());
                }
            }
        }
        StringBuilder homepageSB = new StringBuilder();
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(InitWebServer.class);
        for (Class<?> clazz : annotated) {
            if (clazz.isAnnotationPresent(InitWebServer.class)) {
                InitWebServer annotation = clazz.getAnnotation(InitWebServer.class);
                homepageSB.append(new Heading("Documentation for " + annotation.docRoot(), 1)).append("\n");
                homepageSB.append("Port: ").append(annotation.port()).append("\n");
                for (int x = 0; x < endpointsLinks.size(); x++) {
                    homepageSB.append(new Link(endpointsLinks.get(x), endpointsLinks.get(x))).append("\n");
                }
                homepage = homepageSB.toString();
            }
        }

        ServerNext serverNext = ServerNext.getInstance();
        for (int x = 0; x < endpointsLinks.size(); x++) {
            int finalX = x;
            serverNext.addEndpoint(endpointsLinks.get(x), (GetHandlerNew) (request, requestHeaders, requestParams, clientIp) -> Response.builder()
                    .bodyAsString(endpointsPages.get(finalX))
                    .statusCode(200)
                    .build());
        }
        serverNext.addEndpoint("/docs", (GetHandlerNew) (request, requestHeaders, requestParams, clientIp) -> Response.builder()
                .bodyAsString(homepage)
                .statusCode(200)
                .build());
    }
}
