package net.krlite.pierced.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>Table</h1>
 * Can annotate fields.
 * The target object will be under a table of this annotation's {@link Table#value()}, which is equivalent to a prefixed
 * dotted key to the object's key.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * @return	The table.
	 * Empty values will be ignored, leading, and trailing dots will be trimmed.
	 */
	String value();
}
