package com.andyadc.kratos.context.attribute;

/**
 * 上下文属性简单实现类
 *
 * @param <T>
 */
public final class ContextAttributeKey<T> extends AttributeKey<T> {

    private final Class<T> valueClass;

    ContextAttributeKey(final Class<T> valueClass) {
        super();
        this.valueClass = valueClass;
    }

    @Override
    public T cast(Object value) {
        if (valueClass == null) {
            return null;
        }
        return valueClass.cast(value);
    }

    @Override
    public String toString() {
        if (valueClass != null) {
            return this.getClass().getName() + "<" +
                    valueClass.getName() +
                    ">";
        }
        return super.toString();
    }
}
