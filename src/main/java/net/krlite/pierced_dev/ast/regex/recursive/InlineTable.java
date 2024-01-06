package net.krlite.pierced_dev.ast.regex.recursive;

import net.krlite.pierced_dev.ast.regex.ABNF;
import net.krlite.pierced_dev.ast.regex.Whitespace;
import net.krlite.pierced_dev.ast.regex.key.Key;

import java.util.regex.Pattern;

public class InlineTable extends ABNF {
	public static final Pattern INLINE_TABLE_OPEN = Pattern.compile("\\{");
	public static final Pattern INLINE_TABLE_CLOSE = Pattern.compile("}");
	public static final Pattern INLINE_TABLE_SEP = Pattern.compile(",");

	public static final Pattern INLINE_TABLE_KEYVAL_STARTER = chain(
			Pattern.compile("^"),
			repeats(Whitespace.WS),
			Key.KEY,
			Key.KEYVAL_SEP,
			INLINE_TABLE_OPEN
	);
	public static final Pattern INLINE_TABLE_VALUE_STARTER = chain(
			Pattern.compile("^"),
			INLINE_TABLE_OPEN
	);
}
