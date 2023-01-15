package io.github.prismwork.prismconfig.api.config;

import io.github.prismwork.prismconfig.impl.config.DefaultDeserializersImpl;

import java.util.function.Function;

/**
 * The default deserializers Prism Config provides.
 * <p>For now there are three: JSON, JSON5 and TOML (0.4.0).
 *
 * @since 0.1.0
 */
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
     * @param clazz the config class the deserializer is going to handle
     * @param <T> the type of the config class
     * @return the JSON deserializer for the given config class
     */
    <T> Function<T, String> json(Class<T> clazz);

    /**
     * Returns a JSON5 deserializer for the given config class.
     * <p>Note that currently JSON5 deserializer only works with
     * {@link io.github.prismwork.prismconfig.api.annot.Comment.BeforeLine} if you want to add comments.
     *
     * @param clazz the config class the deserializer is going to handle
     * @param <T> the type of the config class
     * @return the JSON5 deserializer for the given config class
     */
    <T> Function<T, String> json5(Class<T> clazz);

    /**
     * Returns a TOML (0.4.0) deserializer for the given config class.
     *
     * @param clazz the config class the deserializer is going to handle
     * @param <T> the type of the config class
     * @return the TOML deserializer for the given config class
     */
    <T> Function<T, String> toml(Class<T> clazz);
}
