package net.krlite.pierced_dev.test;

import net.krlite.pierced_dev.ast.regex.ABNF;
import net.krlite.pierced_dev.ast.regex.Structures;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegexTest {
	public static void main(String[] args) {
		testJoin();
		System.out.println(Structures.TOML.matcher("[table]\ra = 1\r'b' = '''\rliteral\rstring''' # Comment").matches());
	}

	public static void testJoin() {
		final Pattern a = Pattern.compile("[A-Za-z]");
		final Pattern b = Pattern.compile("[0-9]");
		final Pattern c = Pattern.compile("=");

		assert Objects.equals(
				ABNF.join(a, b, c).pattern(),
				"[A-Za-z0-9=]"
		);
	}
}
