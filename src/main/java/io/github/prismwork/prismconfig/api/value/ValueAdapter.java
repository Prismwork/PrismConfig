package io.github.prismwork.prismconfig.api.value;

import java.lang.reflect.Field;

public class ValueAdapter<T, P> {
    private final Setter<P> setter;
    private final Writer<T, P> writer;

    public ValueAdapter(Setter<P> setter, Writer<T, P> writer) {
        this.setter = setter;
        this.writer = writer;
    }

    public final void set(P parser, String annot, Field field) throws IllegalAccessException {
        setter.setValue(parser, annot, field);
    }

    public final String write(T value, P parser) {
        return writer.writeValue(value, parser);
    }

    public String write(Class<T> clazz, Object value, P parser) throws ClassCastException {
        return writer.writeValue((clazz.cast(value)), parser);
    }

    public interface Setter<P> {
        void setValue(P parser, String annot, Field field) throws IllegalAccessException ;
    }

    public interface Writer<T, P> {
        String writeValue(T value, P parser);
    }
}
