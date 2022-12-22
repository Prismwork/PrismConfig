# Prism Config

A lightweight config library for Java to let you write your config elegantly and flexibly.

### Getting Started

To add Prism Config to your project, you need to add following into your `build.gradle`:

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
    implementation "io.github.prismwork:prismconfig:0.1.1"
    // Or use the full jar if you don't have the libraries included in your project (Gson, Jankson...)
    // implementation "io.github.prismwork:prismconfig:0.1.1:all"
}
```

Prism Config by default provides serializers and deserializers for JSON (Gson) and JSON5 (Jankson).

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
