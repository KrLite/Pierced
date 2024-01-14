package net.krlite.pierced.annotation;

import java.lang.annotation.*;

/**
 * <h1>Comment</h1>
 * Can annotate fields and types.
 * Can be repeated.
 * The target object will append a comment of this annotation's {@link Comment#value()}.
 *
 * @see Comments
 * @see InlineComment
 * @see TableComment
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Comments.class)
public @interface Comment {
	/**
	 * @return  The comment.
	 * If empty, a new line will be appended.
	 * To add a line of empty comment (such as a line starting with a single <code>#</code> symbol),
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
