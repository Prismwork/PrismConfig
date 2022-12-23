<div align="center">

<img alt="Icon" src="arts/prismconfig_title_lowheight.png" height="250" width="960">

![java8](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/cozy/built-with/java8_vector.svg)
![gradle](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@2/assets/cozy/built-with/gradle_vector.svg)
[![Release](https://img.shields.io/github/v/release/Prismwork/PrismConfig?style=for-the-badge&include_prereleases&sort=semver)][releases]

[English](README.md) | **简体中文**

让你能够高效且优雅地编写你的配置文件的轻量级配置库。

</div>

### 与我共舞

要将 Prism Config 加入你的项目，需要在你的 `build.gradle(.kts)` 中加入以下内容:

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
    // 如果你已经在你的项目中包含了 Prism Config 所需要的库的话，也可以选用较小的 Jar。 (Gson, Jankson...)
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
    // 如果你已经在你的项目中包含了 Prism Config 所需要的库的话，也可以选用较小的 Jar。 (Gson, Jankson...)
    // implementation("io.github.prismwork:prismconfig:0.2.0")
}
```

Prism Config 默认提供了适用于 JSON (Gson)，JSON5 (Jankson) 和 TOML 0.4.0 (toml4j) 的序列化器和反序列化器。

可以通过以下代码来实现把字符串转换成配置文件对象:

```java
String content;
MyConfig config = PrismConfig.getInstance().serialize(
        MyConfig.class,
        content,
        DefaultSerializers.getInstance().json5(MyConfig.class) // 我们假定你的配置文件是用 JSON5 编写的
);
```

可以通过以下代码来实现把配置文件对象转换成字符串:

```java
MyConfig content;
String config = PrismConfig.getInstance().deserialize(
        MyConfig.class,
        content,
        DefaultDeserializers.getInstance().json5(MyConfig.class) // 我们假定你的配置文件是用 JSON5 编写的
);
```

你也可以把它直接写入文件:

```java
MyConfig content;
File configFile;
PrismConfig.getInstance().deserializeAndWrite(
        MyConfig.class,
        content,
        DefaultDeserializers.getInstance().json5(MyConfig.class), // 我们假定你的配置文件是用 JSON5 编写的
        configFile
);
```

要编写你自己的序列化/反序列化器，你可以使用以下代码（我们以序列化为例）:

```java
String content;
PrismConfig.getInstance().serialize(
        MyConfig.class,
        content,
        (string) -> {
            // 在此自行解析
        }
);
```

### 使用的库

* falkreon 制作的 [Jankson](https://github.com/falkreon/Jankson)，以 MIT 协议开源。
* Google 制作的 [Gson](https://github.com/google/gson)，以 Apache-2.0 协议开源。
* Moandji Ezana 制作的 [toml4j](https://github.com/mwanji/toml4j)，以 MIT 协议开源。

### 星标历史

[![Star History Chart](https://api.star-history.com/svg?repos=Prismwork/PrismConfig&type=Date)](https://star-history.com/#Prismwork/PrismConfig)

[releases]: https://github.com/Prismwork/PrismConfig/releases