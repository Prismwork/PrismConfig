package io.github.prismwork.prismconfig.impl.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.github.prismwork.prismconfig.api.config.DefaultSerializers;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public final class DefaultSerializersImpl implements DefaultSerializers {
    public static @Nullable DefaultSerializers INSTANCE_CACHE;

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
                T ret = gson.fromJson(content, clazz);
                if (ret == null) {
                    return clazz.getDeclaredConstructor().newInstance();
                }
                return ret;
            } catch (JsonSyntaxException |
                     InvocationTargetException |
                     InstantiationException |
                     IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException("Failed to parse JSON", e);
            }
        };
    }

    @Override
    public <T> Function<String, T> json5(Class<T> clazz) {
        return (content) -> {
            try {
                T ret = jankson.fromJson(content, clazz);
                if (ret == null) {
                    return clazz.getDeclaredConstructor().newInstance();
                }
                return ret;
            } catch (SyntaxError |
                     InvocationTargetException |
                     InstantiationException |
                     IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException("Failed to parse JSON5", e);
            }
        };
    }
}
