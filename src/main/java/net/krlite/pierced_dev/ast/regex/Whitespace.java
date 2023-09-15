package net.krlite.pierced_dev.ast.regex;

import java.util.regex.Pattern;

public class Whitespace extends ABNF {
	public static final Pattern WSCHAR = Pattern.compile("[ \\t]");
	public static final Pattern WS = repeats(WSCHAR);
}
