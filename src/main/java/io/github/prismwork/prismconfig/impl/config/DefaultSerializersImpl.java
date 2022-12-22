package io.github.prismwork.prismconfig.impl.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.github.prismwork.prismconfig.api.config.DefaultSerializers;

import java.util.function.Function;

public final class DefaultSerializersImpl implements DefaultSerializers {
    public static DefaultSerializers INSTANCE_CACHE;
    private final Gson gson;
    private final Jankson jankson;

    public DefaultSerializersImpl() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.jankson = new Jankson.Builder().build();
        DefaultSerializersImpl.INSTANCE_CACHE = this;
    }

    @Override
    public <T> Function<String, T> json(Class<T> clazz) {
        return (content) -> {
            try {
                return gson.fromJson(content, clazz);
            } catch (JsonSyntaxException e) {
                throw new RuntimeException("Failed to parse JSON5", e);
            }
        };
    }

    @Override
    public <T> Function<String, T> json5(Class<T> clazz) {
        return (content) -> {
            try {
                return jankson.fromJson(content, clazz);
            } catch (SyntaxError e) {
                throw new RuntimeException("Failed to parse JSON5", e);
            }
        };
    }
}
