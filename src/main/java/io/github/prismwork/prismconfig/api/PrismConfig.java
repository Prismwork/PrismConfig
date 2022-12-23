package io.github.prismwork.prismconfig.api;

import io.github.prismwork.prismconfig.impl.PrismConfigImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
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
        if (PrismConfigImpl.INSTANCE_CACHE != null) {
            return PrismConfigImpl.INSTANCE_CACHE;
        }
        return new PrismConfigImpl();
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
     * Cast the given config content to an instance of the config whose type is specified by the "clazz" parameter and cache the serializer for the given type.
     *
     * @param clazz the class of the config instance type
     * @param file the content of the config as a file
     * @param serializer the serializer used to parse the config string
     * @param <T> the type of the config instance
     * @return an instance of the config
     */
    default <T> T serialize(Class<T> clazz, File file, Function<String, T> serializer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String content = Utils.readFromFile(reader);
            return serialize(clazz, content, serializer);
        } catch (IOException e) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException |
                     InstantiationException |
                     IllegalAccessException |
                     NoSuchMethodException ex) {
                throw new RuntimeException("Failed to parse config", ex);
            }
        }
    }

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
     * Cast the given config content to an instance of the config whose type is specified by the "clazz" parameter and cache the serializer for the given type.
     *
     * @param clazz the class of the config instance type
     * @param file the content of the config as a file
     * @param <T> the type of the config instance
     * @return an instance of the config
     */
    default <T> T serializeCached(Class<T> clazz, File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String content = Utils.readFromFile(reader);
            return serializeCached(clazz, content);
        } catch (IOException e) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InvocationTargetException |
                     InstantiationException |
                     IllegalAccessException |
                     NoSuchMethodException ex) {
                throw new RuntimeException("Failed to parse config", ex);
            }
        }
    }

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
    default <T> void deserializeAndWrite(Class<T> clazz, T content, Function<T, String> deserializer, File file) {
        String string = deserialize(clazz, content, deserializer);
        Utils.writeToConfigFile(file, string);
    }

    /**
     * Write the given config instance to the target file as a string representing the config content, using the cached deserializer.
     * <p>If the deserializer for this class is not cached, a {@link RuntimeException} is thrown.
     *
     * @param clazz the class of the config instance type
     * @param content the content of the config as an instance
     * @param file the target file the config is written to
     * @param <T> the type of the config instance
     */
    default <T> void deserializeAndWriteCached(Class<T> clazz, T content, File file) {
        String string = deserializeCached(clazz, content);
        Utils.writeToConfigFile(file, string);
    }

    @ApiStatus.Internal
    class Utils {
        private static @NotNull String readFromFile(@NotNull BufferedReader reader) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();

            return stringBuilder.toString();
        }

        private static void writeToConfigFile(@NotNull File file, String string) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create file", e);
                }
            }
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(""); // Empty the file
                writer.write(string);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException("Failed to write config");
            }
        }
    }
}
