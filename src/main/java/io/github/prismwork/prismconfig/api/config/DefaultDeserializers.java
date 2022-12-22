package io.github.prismwork.prismconfig.api.config;

import io.github.prismwork.prismconfig.impl.config.DefaultDeserializersImpl;

import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("unused")
public interface DefaultDeserializers {
    /**
     * Lazily get the instance of {@link DefaultDeserializers}.
     *
     * @return an instance of {@link DefaultDeserializers}.
     * If the instance cache does not exist, it will try to create one.
     */
    static DefaultDeserializers getInstance() {
        if (DefaultDeserializersImpl.INSTANCE_CACHE != null) {
            return DefaultDeserializersImpl.INSTANCE_CACHE;
        }
        return new DefaultDeserializersImpl();
    }

    /**
     * Returns a JSON deserializer for the given config class.
     *
     * @param clazz the config class the serializer is going to handle
     * @param <T> the type of the config class
     * @return the JSON deserializer for the given config class
     */
    <T> Function<T, String> json(Class<T> clazz);

    /**
     * Returns a JSON5 deserializer for the given config class.
     *
     * @param clazz the config class the serializer is going to handle
     * @param <T> the type of the config class
     * @return the JSON5 deserializer for the given config class
     */
    <T> Function<T, String> json5(Class<T> clazz);
}
