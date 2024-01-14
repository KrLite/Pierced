package net.krlite.pierced.annotation;

import java.lang.annotation.*;

/**
 * <h1>InlineComment</h1>
 * Can annotate fields.
 * The target object will append an inline comment of this annotation's {@link InlineComment#value()}.
 *
 * @see Comment
 * @see TableComment
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InlineComment {
    /**
     * @return  The comment.
     * If empty, nothing will be appended.
     * To add an empty comment (such as an inline comment starting with a single <code>#</code> symbol),
     * use <code>value = " "</code>.
     *
     * <ul>
     *     <li>
     *         <code>Default: </code> ""
     *     </li>
     * </ul>
     */
    String value() default "";
}
