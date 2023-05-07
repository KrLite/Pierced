### <p align=right>[`→` JitPack](https://jitpack.io/#KrLite/Pierced)</p>

<picture>
    	<source media="(prefers-color-scheme: dark)" srcset="/artwork/banner-dim.png?raw=true" />
    	<img src="/artwork/banner-bright.png?raw=true" />
</picture>

<br />
<br />

**Pierced** is a lightweight library for java which handles extremely simple [`TOML`](https://toml.io) configuration files.

## TL;DR

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "com.github.KrLite:Pierced:v?"
}
```

> The `v?` above should be replaced by the latest [`tag name`](https://github.com/KrLite/Pierced/tags) of **Pierced.**

After implementation, you should extend the `Pierced` class and add your own config fields.

## Intro

**Pierced is only 16KB.[^size]** You can bring **Pierced** anywhere you want.

[^size]: The compiled jar of **Pierced** is currently 16KB, and the sources jar is only 8KB.

The sacrifice of such a small size is that **Pierced** is not a full-featured `TOML` parser. It only supports the most basic features of `TOML,` and it is not recommended to use it for complex configuration files. But for those who want a fast, simple one, **Pierced** is of no doubt a good choice.

## Usage

### `1` Create your config class which extends `Pierced.class:`

```java
public class Config extends Pierced {
	public String name;
	public int age;
	public boolean isMale;
	public String[] hobbies;
	public List<String> friends;
	public Map<String, Integer> scores;

	public Config(File file) {
		super(Config.class, file); // Config.class should be the same as this class
	}
}
```

### `2` Create a `Config` instance and load the config file:

```java
Config config = new Config(new File("config.toml"));
```

```java
config.load();
```

### `3` Use and save the config:

```java
System.out.println(config.name);
config.age = 18;
```
```java
config.save();
```

## Compatibility

**Pierced** supports most of the `TOML` features:

- Basic strings and literal strings[^literal_strings]
- Multiline basic strings and literal strings[^literal_strings]

[^literal_strings]: Literal strings can be read, but they will be seen the same as basic strings for now. All the strings are saved as literal strings in case of complex escaping.

- Boolean values
- Integer(bin, oct, dec and hex) values and float values with underscores[^scientific_notation]

[^scientific_notation]: Scientific notation is not supported yet.

- (±)nan and (±)infinity
- Full-line comments and inline comments
- Tables(as dotted keys)

**What Pierced does not support for now:**

- Dates and times
- Array values

## Annotations

**Pierced** supports annotations to customize the behavior of the options.

### `@Comment` to comment:

```java
@Comment("THIS CONFIGURATION FILE IS FOR DEMO")
@Comment("DO NOT USE IN PRODUCTION")
public class Config extends Pierced<Config> {
	public Config(File file) {
		super(Config.class, file);
	}
	
	@Comment("The name of the user")
	public String name = "Username";
}
```

Result:

```toml
# THIS CONFIGURATION FILE IS FOR DEMO
# DO NOT USE IN PRODUCTION

name = 'Username'
# The name of the user
```

### `@Table` to categorize fields:

```java
@Table("user")
public String name = "Username"; // Categorized

public int people = 100; // Uncategorized
```

Result:

```toml
people = 100

[user]
name = 'Username'
```

### `@Silent` to hide fields from saving and loading:

```java
public String name = "Username"; // Visible
public @Silent int age = 18; // Invisible
```

Only the fields not annotated by `@Silent` will be saved and loaded.

## License

**Pierced** is available under **[GNU Leeser Public License.](LICENSE)**
