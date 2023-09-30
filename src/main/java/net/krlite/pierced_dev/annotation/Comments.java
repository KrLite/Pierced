package net.krlite.pierced_dev.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * <h1>Comments</h1>
 * Can annotate fields and types.
 * Plural of {@link Comment}.
 *
 * @see Comment
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Comments {
	/**
	 * @return  The {@link Comment}s.
	 */
	Comment[] value();
}
