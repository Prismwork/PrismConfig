package io.github.prismwork.prismconfig.api;

import io.github.prismwork.prismconfig.impl.PrismConfigImpl;

import java.io.File;
import java.util.Objects;
import java.util.function.Function;

/**
 * The core of Prism Config library, involving the basic config parse utilities.
 * <p>
 * To work with it, you can get an instance via the {@code getInstance()} method:
 * <pre>{@code
 * PrismConfig prism = PrismConfig.getInstance();
 * }</pre>
 * To parse a config, you may simply call the {@code serialize()} method (we assume that your config is written in JSON):
 * <pre>{@code
 * String content;
 * MyConfig config = prism.serialize(MyConfig.class, content, DefaultSerializers.getInstance().json(MyConfig.class));
 * }</pre>
 * To convert a config instance to a string, you may simply call the {@code deserialize()} method (we assume that your config is written in JSON):
 * <pre>{@code
 * MyConfig config;
 * String content = prism.deserialize(MyConfig.class, config, DefaultDeserializers.getInstance().json(MyConfig.class));
 * }</pre>
 * You can also make your own serializer/deserializer. For example:
 * <pre>{@code
 * prism.serialize(MyConfig.class, content, (string) -> {
 *     // Implement your own serializing mechanics here
 * });
 * }</pre>
 * To serialize comments, Prism Config uses {@link blue.endless.jankson.Comment} from Jankson,
 * so if you want to make your own comment parser, you should use this as well.
 *
 * @since 0.1.0
 */
@SuppressWarnings("unused")
public interface PrismConfig {
    /**
     * Lazily get the instance of {@link PrismConfig}.
     *
     * @return an instance of {@link PrismConfig}.
     * If the instance cache does not exist, it will try to create one.
     */
    static PrismConfig getInstance() {
        return Objects.requireNonNullElseGet(
                PrismConfigImpl.INSTANCE_CACHE,
                PrismConfigImpl::new
        );
    }

    /**
     * Cast the given config content to an instance of the config whose type is specified by the "clazz" parameter and cache the serializer for the given type.
     *
     * @param clazz the class of the config instance type
     * @param content the content of the config as a string
     * @param serializer the serializer used to parse the config string
     * @param <T> the type of the config instance
     * @return an instance of the config
     */
    <T> T serialize(Class<T> clazz, String content, Function<String, T> serializer);

    /**
     * Cast the given config content to an instance of the config whose type is specified by the "clazz" parameter, using the cached serializer.
     * <p>If the serializer for this class is not cached, a {@link RuntimeException} is thrown.
     *
     * @param clazz the class of the config instance type
     * @param content the content of the config as a string
     * @param <T> the type of the config instance
     * @return an instance of the config
     */
    <T> T serializeCached(Class<T> clazz, String content);

    /**
     * Convert the given config instance to a string representing the config content and cache the deserializer for the given type.
     *
     * @param clazz the class of the config instance type
     * @param content the content of the config as an instance
     * @param deserializer the deserializer used to parse the config instance
     * @param <T> the type of the config instance
     * @return the content of the config as a string
     */
    <T> String deserialize(Class<T> clazz, T content, Function<T, String> deserializer);

    /**
     * Convert the given config instance to a string representing the config content, using the cached deserializer.
     * <p>If the deserializer for this class is not cached, a {@link RuntimeException} is thrown.
     *
     * @param clazz the class of the config instance type
     * @param content the content of the config as an instance
     * @param <T> the type of the config instance
     * @return the content of the config as a string
     */
    <T> String deserializeCached(Class<T> clazz, T content);

    /**
     * Write the given config instance to the target file as a string representing the config content and cache the deserializer for the given type.
     *
     * @param clazz the class of the config instance type
     * @param content the content of the config as an instance
     * @param deserializer the deserializer used to parse the config instance
     * @param file the target file the config is written to
     * @param <T> the type of the config instance
     */
    <T> void deserializeAndWrite(Class<T> clazz, T content, Function<T, String> deserializer, File file);

    /**
     * Write the given config instance to the target file as a string representing the config content, using the cached deserializer.
     * <p>If the deserializer for this class is not cached, a {@link RuntimeException} is thrown.
     *
     * @param clazz the class of the config instance type
     * @param content the content of the config as an instance
     * @param file the target file the config is written to
     * @param <T> the type of the config instance
     */
    <T> void deserializeAndWriteCached(Class<T> clazz, T content, File file);
}
