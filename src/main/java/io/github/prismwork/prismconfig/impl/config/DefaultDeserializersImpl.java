package io.github.prismwork.prismconfig.impl.config;

import blue.endless.jankson.Jankson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.prismwork.prismconfig.api.config.DefaultDeserializers;

import java.util.function.Function;

public final class DefaultDeserializersImpl implements DefaultDeserializers {
    public static DefaultDeserializers INSTANCE_CACHE;

    private final Gson gson;
    private final Jankson jankson;

    public DefaultDeserializersImpl() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.jankson = new Jankson.Builder().build();
        DefaultDeserializersImpl.INSTANCE_CACHE = this;
    }

    @Override
    public <T> Function<T, String> json(Class<T> clazz) {
        return gson::toJson;
    }

    @Override
    public <T> Function<T, String> json5(Class<T> clazz) {
        return (config) -> jankson.toJson(config).toJson();
    }
}
