package net.krlite.pierced_dev.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TableComments.class)
public @interface TableComment {
    String table();
    String comment();
}
