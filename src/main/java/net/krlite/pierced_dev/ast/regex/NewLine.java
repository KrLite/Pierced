package net.krlite.pierced_dev.ast.regex;

import java.util.regex.Pattern;

public class NewLine extends ABNF {
	public static final Pattern NEWLINE = Pattern.compile("(\\r\\n)|(\\n)|(\\r)");
}
