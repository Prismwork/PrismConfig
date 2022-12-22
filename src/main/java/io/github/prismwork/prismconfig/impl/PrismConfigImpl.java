package io.github.prismwork.prismconfig.impl;

import io.github.prismwork.prismconfig.api.PrismConfig;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
@SuppressWarnings("unchecked")
public final class PrismConfigImpl implements PrismConfig {
    public static @Nullable PrismConfig INSTANCE_CACHE;

    private final Map<Class<?>, Function<String, Object>> cachedSerializers;
    private final Map<Class<?>, Function<Object, String>> cachedDeserializers;

    public PrismConfigImpl() {
        cachedSerializers = new HashMap<>();
        cachedDeserializers = new HashMap<>();
        PrismConfigImpl.INSTANCE_CACHE = this;
    }

    @Override
    public <T> T serialize(Class<T> clazz, String content, Function<String, T> serializer) {
        if (!cachedSerializers.containsKey(clazz)) {
            cachedSerializers.put(clazz, (Function<String, Object>) serializer);
        }
        return serializer.apply(content);
    }

    @Override
    public <T> T serializeCached(Class<T> clazz, String content) {
        if (!cachedSerializers.containsKey(clazz)) throw new RuntimeException("Cached serializer not found");
        return (T) cachedSerializers.get(clazz).apply(content);
    }

    @Override
    public <T> String deserialize(Class<T> clazz, T content, Function<T, String> deserializer) {
        if (!cachedDeserializers.containsKey(clazz)) {
            cachedDeserializers.put(clazz, (Function<Object, String>) deserializer);
        }
        return deserializer.apply(content);
    }

    @Override
    public <T> String deserializeCached(Class<T> clazz, T content) {
        if (!cachedDeserializers.containsKey(clazz)) throw new RuntimeException("Cached deserializer not found");
        return cachedDeserializers.get(clazz).apply(content);
    }
}
