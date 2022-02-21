package com.github.vizaizai.remote.serializer;

/**
 * 对象序列化器
 * @author liaochongwei
 * @date 2022/2/18 11:46
 */
public interface Serializer {
    /**
     * 序列化
     * @param obj 序列化
     * @return 字节码
     */
     byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes 字节码数组
     * @param clazz 对象类型
     * @return 对象
     */
    <T> Object deserialize(byte[] bytes, Class<T> clazz);
}
