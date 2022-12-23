<div align="center">

<img alt="Icon" src="arts/prismconfig_title_lowheight.png" height="250" width="960">

![java8](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/cozy/built-with/java8_vector.svg)
![gradle](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/cozy/built-with/gradle_vector.svg)
[![Release](https://img.shields.io/github/v/release/Prismwork/PrismConfig?style=for-the-badge&include_prereleases&sort=semver)][releases]

**English** | [简体中文](README_ZH.md)

A lightweight config library for Java to let you write your config elegantly and flexibly.

</div>

### Getting Started

To add Prism Config to your project, you need to add following into your `build.gradle(.kts)`:

Groovy DSL:
```groovy
repositories {
    // ...
    maven {
        name = "Nova Committee - Release"
        url = "https://maven.nova-committee.cn/releases/"
    }
    maven {
        name = "Nova Committee - Snapshot"
        url = "https://maven.nova-committee.cn/snapshots/"
    }
}

dependencies {
    // ...
    implementation "io.github.prismwork:prismconfig:0.2.0:all"
    // Or use the slim jar if you have the libraries included in your project (Gson, Jankson...)
    // implementation "io.github.prismwork:prismconfig:0.2.0"
}
```

Kotlin DSL:
```kotlin
repositories {
    // ...
    maven {
        name = "Nova Committee - Release"
        url = uri("https://maven.nova-committee.cn/releases/")
    }
    maven {
        name = "Nova Committee - Snapshot"
        url = uri("https://maven.nova-committee.cn/snapshots/")
    }
}

dependencies {
    // ...
    implementation("io.github.prismwork:prismconfig:0.2.0:all")
    // Or use the slim jar if you have the libraries included in your project (Gson, Jankson...)
    // implementation("io.github.prismwork:prismconfig:0.2.0")
}
```

Prism Config by default provides serializers and deserializers for JSON (Gson), JSON5 (Jankson) and TOML 0.4.0 (toml4j).

To parse a config from string into object, you can do this:

```java
String content;
MyConfig config = PrismConfig.getInstance().serialize(
        MyConfig.class,
        content,
        DefaultSerializers.getInstance().json5(MyConfig.class) // We assume that your config is written in JSON5
);
```

To parse a config from object into string, you can do this:

```java
MyConfig content;
String config = PrismConfig.getInstance().deserialize(
        MyConfig.class,
        content,
        DefaultDeserializers.getInstance().json5(MyConfig.class) // We assume that your config is written in JSON5
);
```

You can also write it to a file:

```java
MyConfig content;
File configFile;
PrismConfig.getInstance().deserializeAndWrite(
        MyConfig.class,
        content,
        DefaultDeserializers.getInstance().json5(MyConfig.class), // We assume that your config is written in JSON5
        configFile
);
```

To write your own serializer/deserializer, you can use the following code (we use serializing as an example):

```java
String content;
PrismConfig.getInstance().serialize(
        MyConfig.class,
        content,
        (string) -> {
            // Do your own parsing here
        }
);
```

### Libraries Used

* [Jankson](https://github.com/falkreon/Jankson) by falkreon, licensed under MIT.
* [Gson](https://github.com/google/gson) by Google, licensed under Apache-2.0.
* [toml4j](https://github.com/mwanji/toml4j) by Moandji Ezana, licensed under MIT.

### Star History

[![Star History Chart](https://api.star-history.com/svg?repos=Prismwork/PrismConfig&type=Date)](https://star-history.com/#Prismwork/PrismConfig)

[releases]: https://github.com/Prismwork/PrismConfig/releases
