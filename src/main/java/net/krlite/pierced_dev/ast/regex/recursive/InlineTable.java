package net.krlite.pierced_dev.ast.regex.recursive;

import net.krlite.pierced_dev.ast.regex.ABNF;

import java.util.regex.Pattern;

public class InlineTable extends ABNF {
	public static final Pattern INLINE_TABLE_OPEN = Pattern.compile("\\{");
	public static final Pattern INLINE_TABLE_CLOSE = Pattern.compile("}");
	public static final Pattern INLINE_TABLE_SEP = Pattern.compile(",");
}
