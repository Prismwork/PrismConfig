package io.github.prismwork.prismconfig.api.config;

import io.github.prismwork.prismconfig.impl.config.DefaultSerializersImpl;

import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("unused")
public interface DefaultSerializers {
    /**
     * Lazily get the instance of {@link DefaultSerializers}.
     *
     * @return an instance of {@link DefaultSerializers}.
     * If the instance cache does not exist, it will try to create one.
     */
    static DefaultSerializers getInstance() {
        if (DefaultSerializersImpl.INSTANCE_CACHE != null) {
            return DefaultSerializersImpl.INSTANCE_CACHE;
        }
        return new DefaultSerializersImpl();
    }

    /**
     * Returns a JSON serializer for the given config class.
     *
     * @param clazz the config class the serializer is going to handle
     * @param <T> the type of the config class
     * @return the JSON serializer for the given config class
     */
    <T> Function<String, T> json(Class<T> clazz);

    /**
     * Returns a JSON5 serializer for the given config class.
     *
     * @param clazz the config class the serializer is going to handle
     * @param <T> the type of the config class
     * @return the JSON5 serializer for the given config class
     */
    <T> Function<String, T> json5(Class<T> clazz);
}
