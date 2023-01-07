package example;

import net.krlite.pierced.annotation.Comment;
import net.krlite.pierced.config.Pierced;
import net.krlite.pierced.core.EnumLocalizable;

import java.io.File;

@Comment("ABC")
public class Config extends Pierced<Config> {
	public Config(File file) {
		super(Config.class, file);
	}

	public ExEnum exEnum = ExEnum.A;

	enum ExEnum implements EnumLocalizable {
		A("a"), B("b"), C("c");

		private final String name;


		ExEnum(String name) {
			this.name = name;
		}

		@Override
		public String getLocalizedName() {
			return name;
		}
	}
}
