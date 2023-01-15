package io.github.prismwork.prismconfig.impl.config;

import blue.endless.jankson.Jankson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.TomlWriter;
import io.github.prismwork.prismconfig.api.annot.Comment;
import io.github.prismwork.prismconfig.api.config.DefaultDeserializers;
import io.github.prismwork.prismconfig.impl.json5.marshall.MarshallerCustomImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public final class DefaultDeserializersImpl implements DefaultDeserializers {
    public static @Nullable DefaultDeserializers INSTANCE_CACHE;

    private final Gson gson;
    private final Jankson jankson;
    private final TomlWriter toml;

    public DefaultDeserializersImpl() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.jankson = new Jankson.Builder().build();
        this.toml = new TomlWriter.Builder().build();
        DefaultDeserializersImpl.INSTANCE_CACHE = this;
    }

    @Override
    public <T> Function<T, String> json(Class<T> clazz) {
        return gson::toJson;
    }

    @Override
    public <T> Function<T, String> json5(Class<T> clazz) {
        return (config) -> jankson.toJson(config, MarshallerCustomImpl.getFallback()).toJson(true, true);
    }

    @Override
    public <T> Function<T, String> toml(Class<T> clazz) {
        // toml4j does not support adding comments, so we will do our own
        return (config) -> {
            try {
                StringBuilder sb = new StringBuilder();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true); // Make protected and private fields accessible
                    if (!Modifier.isStatic(field.getModifiers())
                            && !Modifier.isTransient(field.getModifiers())) { // Ignore static fields, we don't need them
                        if (!field.getType().isPrimitive() && !field.getType().equals(String.class)) {
                            sb.append("\n"); // Append a new line if the presented field is not of a primitive type or of a string type
                        }
                        if (field.isAnnotationPresent(Comment.BeforeLine.class)) {
                            String value = field.getAnnotation(Comment.BeforeLine.class).value();
                            Arrays.stream(value.split("\n")).forEach(string
                                    -> sb.append("# ").append(string).append("\n"));
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put(field.getName(), field.get(config));
                        String line = toml.write(map);
                        if (field.isAnnotationPresent(Comment.LineEnd.class)) {
                            line = line.replace("\n", "") +
                                    " # " +
                                    field.getAnnotation(Comment.LineEnd.class)
                                            .value()
                                            .replace("\n", " ") +
                                    "\n";
                        }
                        sb.append(line);
                    }
                }
                return sb.toString();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
