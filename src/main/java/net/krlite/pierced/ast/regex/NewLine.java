package net.krlite.pierced.ast.regex;

import java.util.regex.Pattern;

public class NewLine extends ABNF {
	public static final Pattern NEWLINE = or(
			Pattern.compile("\\r\\n"),
			Pattern.compile("\\n"),
			Pattern.compile("\\r")
	);
}
