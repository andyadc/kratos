package com.andyadc.kratos.context.attribute;

/**
 * 网关顶层属性接口
 *
 * @param <T>
 */
public interface GatewayKey<T> {

    /**
     * 将Object对象转换成指定的类型
     */
    T cast(Object obj);
}
