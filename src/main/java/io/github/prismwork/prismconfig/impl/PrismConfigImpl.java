package io.github.prismwork.prismconfig.impl;

import io.github.prismwork.prismconfig.api.PrismConfig;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class PrismConfigImpl implements PrismConfig {
    public static @Nullable PrismConfig INSTANCE_CACHE;

    public PrismConfigImpl() {
        PrismConfigImpl.INSTANCE_CACHE = this;
    }

    @Override
    public <T> T serialize(Class<T> clazz, String content, Function<String, T> serializer) {
        return serializer.apply(content);
    }

    @Override
    public <T> String deserialize(Class<T> clazz, T content, Function<T, String> deserializer) {
        return deserializer.apply(content);
    }
}
