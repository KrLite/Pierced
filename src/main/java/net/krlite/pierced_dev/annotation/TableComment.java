package net.krlite.pierced_dev.annotation;

import java.lang.annotation.*;

/**
 * <h1>TableComment</h1>
 * Can annotate types.
 * Can be repeated.
 * {@link Table}s inside the target type whose {@link Table#value()} has an equivalent key value to this annotation's
 * {@link TableComment#table()} will append a comment of this annotation's {@link TableComment#comment()}.
 *
 * @see TableComments
 * @see Comment
 * @see InlineComment
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TableComments.class)
public @interface TableComment {
    /**
     * @return  The table key.
     */
    String table();

    /**
     * @return  The comment.
     * If empty, a new line will be appended.
     * To add a line of empty comment (such as a line starting with a single <code>#</code> symbol),
     * use <code>comment = " "</code>.
     *
     * <ul>
     *     <li>
     *         <code>Default: </code> ""
     *     </li>
     * </ul>
     */
    String comment() default "";
}
