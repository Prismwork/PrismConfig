package io.github.prismwork.prismconfig.impl.config;

import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.TomlWriter;
import io.github.prismwork.prismconfig.api.config.DefaultDeserializers;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
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
        return (config) -> jankson.toJson(config).toJson(true, true);
    }

    @Override
    public <T> Function<T, String> toml(Class<T> clazz) {
        // toml4j does not support adding comments, so we will do our own, using the @Comment from Jankson
        return (config) -> {
            try {
                StringBuilder sb = new StringBuilder();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true); // Make protected and private fields accessible
                    if (field.isAnnotationPresent(Comment.class)) {
                        sb.append("# ")
                                .append(field.getDeclaredAnnotation(Comment.class).value())
                                .append("\n");
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put(field.getName(), field.get(config));
                    sb.append(toml.write(map));
                }
                return sb.toString();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
