# Prism Config

A lightweight config library for Java to let you write your config elegantly and flexibly.

**WORK IN PROGRESS**

### Getting Started

Prism Config provides serializers and deserializers for JSON (Gson) and JSON5 (Jankson).

To parse a config, you can do this:

```java
String content;
MyConfig config = PrismConfig.getInstance().serialize(
        MyConfig.class,
        content,
        DefaultSerializers.getInstance().json5(MyConfig.class) // We assume that your config is written in JSON5
);
```

**DOCS UNDER CONSTRUCTION**
