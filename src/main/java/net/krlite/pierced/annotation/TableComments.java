package net.krlite.pierced.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>TableComments</h1>
 * Can annotate types.
 * Plural of {@link TableComment}.
 *
 * @see TableComment
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableComments {
    /**
     * @return  The {@link TableComment}s.
     */
    TableComment[] value();
}
