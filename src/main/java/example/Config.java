package example;

import net.krlite.pierced.config.Pierced;

import java.io.File;

public class Config extends Pierced<Config> {
	public Config(File file) {
		super(Config.class, file);
	}

	public String token = "abc";
	public String prefix = "!";
}
