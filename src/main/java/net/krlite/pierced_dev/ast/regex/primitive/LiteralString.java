package net.krlite.pierced_dev.ast.regex.primitive;

import net.krlite.pierced_dev.ast.regex.ABNF;
import net.krlite.pierced_dev.ast.regex.Comment;

import java.util.regex.Pattern;

public class LiteralString extends ABNF {
	public static final Pattern APOSTROPHE = Pattern.compile("'");

	public static final Pattern LITERAL_CHAR = or(
			Pattern.compile("[\\x09\\x20-\\x26\\x28-\\x7E]"),
			Comment.NON_ASCII
	);
	public static final Pattern LITERAL_STRING = chain(
			APOSTROPHE,
			repeats(LITERAL_CHAR),
			APOSTROPHE
	);
}
