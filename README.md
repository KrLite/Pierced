### <p align=right>[`→` JitPack](https://jitpack.io/#KrLite/Pierced)</p>

<picture>
    <source media="(prefers-color-scheme: dark)" srcset="/artwork/banner-bright.png?raw=true" />
    <img align=right height=87 src="/artwork/banner-dim.png?raw=true" />
</picture>

<br />

**Pierced** is a lightweight library for java which handles extremely simple [`TOML`](https://toml.io) configuration files.

## TL;DR

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation include('com.github.KrLite:Pierced:xxx')
}
```

> Please replace `xxx` above with the latest tag name.

## Intro

**Pierced is only 20KB.** You can bring **Pierced** anywhere you want.

The sacrifice of such a small size is that **Pierced** is not a full-featured `TOML` parser. It only supports the most basic features of `TOML,` and it is not recommended to use it for complex configuration files. But for those who want a fast, simple one, **Pierced** is of no doubt a good choice.

## Usage

`1` Create your config class that extends `Pierced.class:`

```java
public class Config extends Pierced<Config> {
    public String name;
    public int age;
    public boolean isMale;
    public String[] hobbies;
    public List<String> friends;
    public Map<String, Integer> scores;
	
	
}
```

`2` Create a `Config` instance and load the config file:

```java
```
