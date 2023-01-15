package io.github.prismwork.prismconfig.impl.json5.marshall;

import blue.endless.jankson.*;
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.DeserializerFunction;
import blue.endless.jankson.api.Marshaller;
import blue.endless.jankson.impl.POJODeserializer;
import blue.endless.jankson.impl.serializer.DeserializerFunctionPool;
import blue.endless.jankson.magic.TypeMagic;
import io.github.prismwork.prismconfig.api.annot.Comment;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class MarshallerCustomImpl implements Marshaller {
    private static final MarshallerCustomImpl INSTANCE = new MarshallerCustomImpl();

    public static Marshaller getFallback() { return INSTANCE; }

    private final Map<Class<?>, Function<Object,?>> primitiveMarshallers = new HashMap<>();
    Map<Class<?>, Function<JsonObject,?>> typeAdapters = new HashMap<>();

    private final Map<Class<?>, BiFunction<Object, Marshaller, JsonElement>> serializers = new HashMap<>();
    private final Map<Class<?>, DeserializerFunctionPool<?>> deserializers = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> typeFactories = new HashMap<>();

    public <T> void register(Class<T> clazz, Function<Object, T> marshaller) {
        primitiveMarshallers.put(clazz, marshaller);
    }

    public <T> void registerTypeAdapter(Class<T> clazz, Function<JsonObject, T> adapter) {
        typeAdapters.put(clazz, adapter);
    }

    @SuppressWarnings("unchecked")
    public <T> void registerSerializer(Class<T> clazz, Function<T, JsonElement> serializer) {
        serializers.put(clazz, (it, marshaller)->serializer.apply((T) it));
    }

    @SuppressWarnings("unchecked")
    public <T> void registerSerializer(Class<T> clazz, BiFunction<T, blue.endless.jankson.api.Marshaller, JsonElement> serializer) {
        serializers.put(clazz, (BiFunction<Object, blue.endless.jankson.api.Marshaller, JsonElement>) serializer);
    }

    public <T> void registerTypeFactory(Class<T> clazz, Supplier<T> supplier) {
        typeFactories.put(clazz, supplier);
    }

    public <A,B> void registerDeserializer(Class<A> sourceClass, Class<B> targetClass, DeserializerFunction<A,B> function) {
        @SuppressWarnings("unchecked")
        DeserializerFunctionPool<B> pool = (DeserializerFunctionPool<B>)deserializers.get(targetClass);
        if (pool == null) {
            pool = new DeserializerFunctionPool<>(targetClass);
            deserializers.put(targetClass, pool);
        }
        pool.registerUnsafe(sourceClass, function);
    }

    public MarshallerCustomImpl() {
        register(Void.class, (it)->null);

        register(String.class, Object::toString);

        register(Byte.class, (it)->(it instanceof Number) ? ((Number)it).byteValue() : null);
        register(Character.class, (it)->(it instanceof Number) ? (char)((Number)it).shortValue() : it.toString().charAt(0));
        register(Short.class, (it)->(it instanceof Number) ? ((Number)it).shortValue() : null);
        register(Integer.class, (it)->(it instanceof Number) ? ((Number)it).intValue() : null);
        register(Long.class, (it)->(it instanceof Number) ? ((Number)it).longValue() : null);
        register(Float.class, (it)->(it instanceof Number) ? ((Number)it).floatValue() : null);
        register(Double.class, (it)->(it instanceof Number) ? ((Number)it).doubleValue() : null);
        register(Boolean.class, (it)->(it instanceof Boolean) ? (Boolean)it : null);

        register(Void.TYPE, (it)->null);
        register(Byte.TYPE, (it)->(it instanceof Number) ? ((Number)it).byteValue() : null);
        register(Character.TYPE, (it)->(it instanceof Number) ? (char)((Number)it).shortValue() : it.toString().charAt(0));
        register(Short.TYPE, (it)->(it instanceof Number) ? ((Number)it).shortValue() : null);
        register(Integer.TYPE, (it)->(it instanceof Number) ? ((Number)it).intValue() : null);
        register(Long.TYPE, (it)->(it instanceof Number) ? ((Number)it).longValue() : null);
        register(Float.TYPE, (it)->(it instanceof Number) ? ((Number)it).floatValue() : null);
        register(Double.TYPE, (it)->(it instanceof Number) ? ((Number)it).doubleValue() : null);
        register(Boolean.TYPE, (it)->(it instanceof Boolean) ? (Boolean)it : null);


        registerSerializer(Void.class, (it) -> JsonNull.INSTANCE);
        registerSerializer(Character.class, (it) -> new JsonPrimitive("" + it));
        registerSerializer(String.class, JsonPrimitive::new);
        registerSerializer(Byte.class, (it)->new JsonPrimitive(Long.valueOf(it)));
        registerSerializer(Short.class, (it)->new JsonPrimitive(Long.valueOf(it)));
        registerSerializer(Integer.class, (it)->new JsonPrimitive(Long.valueOf(it)));
        registerSerializer(Long.class, JsonPrimitive::new);
        registerSerializer(Float.class, (it)->new JsonPrimitive(Double.valueOf(it)));
        registerSerializer(Double.class, JsonPrimitive::new);
        registerSerializer(Boolean.class, JsonPrimitive::new);

        registerSerializer(Void.TYPE, (it)->JsonNull.INSTANCE);
        registerSerializer(Character.TYPE, (it)->new JsonPrimitive(""+it));
        registerSerializer(Byte.TYPE, (it)->new JsonPrimitive(Long.valueOf(it)));
        registerSerializer(Short.TYPE, (it)->new JsonPrimitive(Long.valueOf(it)));
        registerSerializer(Integer.TYPE, (it)->new JsonPrimitive(Long.valueOf(it)));
        registerSerializer(Long.TYPE, JsonPrimitive::new);
        registerSerializer(Float.TYPE, (it)->new JsonPrimitive(Double.valueOf(it)));
        registerSerializer(Double.TYPE, JsonPrimitive::new);
        registerSerializer(Boolean.TYPE, JsonPrimitive::new);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T marshall(Type type, JsonElement elem) {
        if (elem == null) return null;
        if (elem == JsonNull.INSTANCE) return null;

        if (type instanceof Class) {
            try {
                return marshall((Class<T>) type, elem);
            } catch (ClassCastException t) {
                return null;
            }
        }

        if (type instanceof ParameterizedType) {
            try {
                Class<T> clazz = (Class<T>) TypeMagic.classForType(type);

                return marshall(clazz, elem);
            } catch (ClassCastException t) {
                return null;
            }
        }

        return null;
    }

    public <T> T marshall(Class<T> clazz, JsonElement elem) {
        try {
            return marshall(clazz, elem, false);
        } catch (Throwable t) {
            return null;
        }
    }

    public <T> T marshallCarefully(Class<T> clazz, JsonElement elem) throws DeserializationException {
        return marshall(clazz, elem, true);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T marshall(Class<T> clazz, JsonElement elem, boolean failFast) throws DeserializationException {
        if (elem == null) return null;
        if (elem == JsonNull.INSTANCE) return null;
        if (clazz.isAssignableFrom(elem.getClass())) return (T)elem; //Already the correct type

        // Externally registered deserializers
        DeserializerFunctionPool<T> pool = (DeserializerFunctionPool<T>)deserializers.get(clazz);
        if (pool!=null) {
            try {
                return pool.apply(elem, this);
            } catch (DeserializerFunctionPool.FunctionMatchFailedException ignored) {}
        }

        // Internally annotated deserializers
        pool = POJODeserializerInternal.findDeserializers(clazz);
        T poolResult;
        try {
            poolResult = pool.apply(elem, this);
            return poolResult;
        } catch (DeserializerFunctionPool.FunctionMatchFailedException ignored) {
        }


        if (Enum.class.isAssignableFrom(clazz)) {
            if (!(elem instanceof JsonPrimitive)) return null;
            String name = ((JsonPrimitive)elem).getValue().toString();

            T[] constants = clazz.getEnumConstants();
            if (constants==null) return null;
            for(T t : constants) {
                if (((Enum<?>)t).name().equals(name)) return t;
            }
        }

        if (clazz.equals(String.class)) {
            // Almost everything has a String representation
            if (elem instanceof JsonObject) return (T) elem.toJson(false, false);
            if (elem instanceof JsonArray) return (T) elem.toJson(false, false);
            if (elem instanceof JsonPrimitive) {
                ((JsonPrimitive) elem).getValue();
                return (T)((JsonPrimitive)elem).asString();
            }
            if (elem instanceof JsonNull) return (T)"null";

            if (failFast) throw new DeserializationException("Encountered unexpected JsonElement type while deserializing to string: "+elem.getClass().getCanonicalName());
            return null;
        }

        if (elem instanceof JsonPrimitive) {
            Function<Object, ?> func = primitiveMarshallers.get(clazz);
            if (func!=null) {
                return (T)func.apply(((JsonPrimitive)elem).getValue());
            } else {
                if (failFast) throw new DeserializationException("Don't know how to unpack value '"+ elem +"' into target type '"+clazz.getCanonicalName()+"'");
                return null;
            }
        } else if (elem instanceof JsonObject) {


            if (clazz.isPrimitive()) throw new DeserializationException("Can't marshall json object into primitive type "+clazz.getCanonicalName());
            if (JsonPrimitive.class.isAssignableFrom(clazz)) {
                if (failFast) throw new DeserializationException("Can't marshall json object into a json primitive");
                return null;
            }

            JsonObject obj = (JsonObject) elem;
            obj.setMarshaller(this);

            if (typeAdapters.containsKey(clazz)) {
                return (T) typeAdapters.get(clazz).apply((JsonObject) elem);
            }

            if (typeFactories.containsKey(clazz)) {
                T result = (T)typeFactories.get(clazz).get();
                try {
                    POJODeserializer.unpackObject(result, obj, failFast);
                    return result;
                } catch (Throwable t) {
                    if (failFast) throw t;
                    return null;
                }
            } else {

                try {
                    T result = TypeMagic.createAndCast(clazz, failFast);
                    POJODeserializer.unpackObject(result, obj, failFast);
                    return result;
                } catch (Throwable t) {
                    if (failFast) throw t;
                    return null;
                }
            }

        } else if (elem instanceof JsonArray) {
            if (clazz.isPrimitive()) return null;
            if (clazz.isArray()) {
                Class<?> componentType = clazz.getComponentType();
                JsonArray array = (JsonArray)elem;

                T result = (T) Array.newInstance(componentType, array.size());
                for(int i=0; i<array.size(); i++) {
                    Array.set(result, i, marshall(componentType, array.get(i)));
                }
                return result;
            }
        }

        return null;
    }

    public JsonElement serialize(Object obj) {
        if (obj == null) return JsonNull.INSTANCE;

        //Prefer exact match
        BiFunction<Object, Marshaller, JsonElement> serializer = serializers.get(obj.getClass());
        if (serializer!=null) {
            JsonElement result = serializer.apply(obj, this);
            if (result instanceof JsonObject) ((JsonObject)result).setMarshaller(this);
            if (result instanceof JsonArray) ((JsonArray)result).setMarshaller(this);
            return result;
        } else {
            //Detailed match
            for(Map.Entry<Class<?>, BiFunction<Object, Marshaller, JsonElement>> entry : serializers.entrySet()) {
                if (entry.getKey().isAssignableFrom(obj.getClass())) {
                    JsonElement result = entry.getValue().apply(obj, this);
                    if (result instanceof JsonObject) ((JsonObject)result).setMarshaller(this);
                    if (result instanceof JsonArray) ((JsonArray)result).setMarshaller(this);
                    return result;
                }
            }
        }

        if (obj instanceof Enum) {
            return new JsonPrimitive(((Enum<?>)obj).name());
        }

        if (obj.getClass().isArray()) {

            JsonArray array = new JsonArray();
            array.setMarshaller(this);
            //Class<?> component = obj.getClass().getComponentType();
            for(int i=0; i<Array.getLength(obj); i++) {
                Object elem = Array.get(obj, i);
                JsonElement parsed = serialize(elem);
                array.add(parsed);
            }
            return array;
        }

        if (obj instanceof Collection) {
            JsonArray array = new JsonArray();
            array.setMarshaller(this);
            for(Object elem : (Collection<?>)obj) {
                JsonElement parsed = serialize(elem);
                array.add(parsed);
            }
            return array;
        }

        if (obj instanceof Map) {
            JsonObject result = new JsonObject();
            for(Map.Entry<?,?> entry : ((Map<?,?>)obj).entrySet()) {
                String k = entry.getKey().toString();
                Object v = entry.getValue();
                result.put(k, serialize(v));
            }
            return result;
        }

        JsonObject result = new JsonObject();
        // Pull in public fields first
        for(Field f : obj.getClass().getFields()) {
            if (Modifier.isStatic(f.getModifiers()) || // Not part of the object
                    Modifier.isTransient(f.getModifiers())) continue; // Never serialize
            parseWithComments(obj, result, f);
        }

        // Add in what private fields we can reach
        for (Field f : obj.getClass().getDeclaredFields()) {
            if (Modifier.isPublic(f.getModifiers()) || // Already serialized
                    Modifier.isStatic(f.getModifiers()) || // Not part of the object
                    Modifier.isTransient(f.getModifiers())) continue; //Never serialize
            parseWithComments(obj, result, f);
        }

        return result;
    }

    private void parseWithComments(Object obj, JsonObject result, Field f) {
        f.setAccessible(true);

        try {
            Object child = f.get(obj);
            String name = f.getName();

            Comment.BeforeLine comment = f.getAnnotation(Comment.BeforeLine.class);
            if (comment == null) {
                result.put(name, serialize(child));
            } else {
                result.put(name, serialize(child), comment.value());
            }
        } catch (IllegalArgumentException | IllegalAccessException ignored) {}
    }

    private static class POJODeserializerInternal extends POJODeserializer {
        public static <B> DeserializerFunctionPool<B> findDeserializers(Class<B> targetClass) {
            return deserializersFor(targetClass);
        }
    }
}
