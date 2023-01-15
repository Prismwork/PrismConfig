package io.github.prismwork.prismconfig.impl.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.moandjiezana.toml.Toml;
import io.github.prismwork.prismconfig.api.config.DefaultSerializers;
import io.github.prismwork.prismconfig.libs.qdcss.QDCSS;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Function;

@ApiStatus.Internal
public final class DefaultSerializersImpl implements DefaultSerializers {
    public static @Nullable DefaultSerializers INSTANCE_CACHE;

    private final Gson gson;
    private final Jankson jankson;
    private final Toml toml;

    public DefaultSerializersImpl() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.jankson = new Jankson.Builder().build();
        this.toml = new Toml();
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

    @Override
    public <T> Function<String, T> toml(Class<T> clazz) {
        return (content) -> {
            try {
                T ret = toml.read(content).to(clazz);
                if (ret == null) {
                    return clazz.getDeclaredConstructor().newInstance();
                }
                return ret;
            } catch (InvocationTargetException |
                     InstantiationException |
                     IllegalAccessException |
                     IllegalStateException |
                     NoSuchMethodException e) {
                throw new RuntimeException("Failed to parse TOML", e);
            }
        };
    }

    @Override
    public <T> Function<String, T> css(Class<T> clazz) {
        return (content) -> {
            try {
                T ret = clazz.getDeclaredConstructor().newInstance();
                QDCSS qdcss = QDCSS.load("", content);
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (!Modifier.isStatic(field.getModifiers())
                            && !Modifier.isTransient(field.getModifiers())) {
                        String name = field.getName();
                        Optional<String> result = qdcss.get(name);
                        if (result.isPresent()) {
                            if (field.getType().equals(String.class)) {
                                field.set(ret, result.get());
                            } else if (field.getType().equals(Integer.TYPE)) {
                                field.setInt(ret, qdcss.getInt(name).get());
                            } else if (field.getType().equals(Boolean.TYPE)) {
                                field.setBoolean(ret, qdcss.getBoolean(name).get());
                            } else if (field.getType().equals(Float.TYPE)) {
                                field.setFloat(ret, qdcss.getDouble(name).get().floatValue());
                            } else if (field.getType().equals(Double.TYPE)) {
                                field.setDouble(ret, qdcss.getDouble(name).get());
                            } else if (field.getType().isEnum()) {

                            }
                        }
                    }
                }
                return ret;
            } catch (InvocationTargetException |
                     InstantiationException |
                     IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
