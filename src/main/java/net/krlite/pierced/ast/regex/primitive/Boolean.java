package net.krlite.pierced.ast.regex.primitive;

import net.krlite.pierced.ast.regex.ABNF;

import java.util.regex.Pattern;

public class Boolean extends ABNF {
	public static final Pattern TRUE = Pattern.compile("true");
	public static final Pattern FALSE = Pattern.compile("false");

	public static final Pattern BOOLEAN = or(
			TRUE,
			FALSE
	);
}
