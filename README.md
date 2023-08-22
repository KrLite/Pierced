### <p align=right>[`→` JitPack](https://jitpack.io/#KrLite/Pierced)</p>

<picture>
	<source media="(prefers-color-scheme: dark)" srcset="/artwork/banner-dim.png?raw=true"/>
	<img src="/artwork/banner-bright.png?raw=true"/>
</picture>

<br/>
<br/>

**Pierced** is a lightweight Java library which handles basic [TOML](https://toml.io) configuration files.

## Gradle

<details>

<summary>Groovy</summary>

###### <p align=right>build.gradle</p>
```groovy
repositories {
	maven { url "https://jitpack.io" }
}

dependencies {
	implementation include("com.github.KrLite:Pierced:$project.pierced_version")
}
```

###### <p align=right>gradle.properties</p>
```
pierced_version=?
```

> [!NOTE]
> Replace `?` with the latest [`tag name`](https://github.com/KrLite/Pierced/tags) of **Pierced**.

</details>

<details>

<summary>Kotlin</summary>

###### <p align=right>build.gradle.kts</p>
```kotlin
repositories {
	maven("https://jitpack.io")
}

dependencies {
	include("com.github.KrLite:Pierced:${property("piercedVersion")}")?.let {
		implementation(it)
	}
}
```

###### <p align=right>gradle.properties</p>
```
piercedVersion=?
```

> [!NOTE]
> Replace `?` with the latest [`tag name`](https://github.com/KrLite/Pierced/tags) of **Pierced**.

</details>

Once implemented, extend `Pierced` class and add your own configuration fields.

## Introduction

**Pierced is only 16KB**[^size]. You can bring **Pierced** anywhere you want.

The sacrifice of such a small size is that **Pierced** is not a full-featured TOML parser. It only supports the most basic features of TOML, and so is not recommended for complex configuration files. Still, for those who want something fast and simple, **Pierced** is a solid choice.

## Usage

### 1. Create a `Config` class which extends `Pierced` class:

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

### 2. Create a `Config` instance and load the configuration file:

```java
Config config = new Config(new File("config.toml"));
```

```java
config.load();
```

### 3. Use and save the configuration:

```java
System.out.println(config.name);
config.age = 18;
```
```java
config.save();
```

## Features

**Pierced** supports most of TOML features:

- (±)nan and (±)infinity
- Basic strings and literal strings[^literal_strings]
- Boolean values
- Full-line comments and inline comments
- Integer (bin, oct, dec and hex) values and float values with underscores[^scientific_notation]
- Multiline basic strings and literal strings[^literal_strings]
- Tables (dotted keys)

> [!WARNING]
> **Pierced** does not support these features for now:

- Array values
- Dates and times

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

#### Result:

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

#### Result:

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

> [!IMPORTANT]
> Only fields not annotated with `@Silent` will be saved and loaded.

## License

**Pierced** is licensed under the **[GNU Lesser General Public License v3.0 or later](LICENSE)**.

[^literal_strings]: Literal strings can be read, but will be seen the same as basic strings for now. All strings are saved as literal strings in case of complex escaping.
[^scientific_notation]: Scientific notation is not supported yet.
[^size]: Currently, **Pierced** compiled and source JARs are only 16KB and 8KB, respectively.
