package ru.zoommax.next.annotation.documentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyDoc {
    String[] name();
    String[] type();
    String[] description();
    boolean[] required();
}
