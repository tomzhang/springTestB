package com.jk51.commons.serialize;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 反序列化库Protostuff 工具类，提供常用的序列化和反序列化操作
 * 作者: wangzhengfei
 * 创建日期: 2017-02-20
 * 修改记录:
 */
public class ProtostuffUtils {

    public static byte[] serialize(Object instance) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        Schema schema = RuntimeSchema.createFrom(instance.getClass());
        return ProtostuffIOUtil.toByteArray(instance, schema, buffer);
    }

    /**
     * 反序列化，需要注意类型T需要有无参的构造方法
     * @param clazz 目前类型的Class对象
     * @param bytes 序列化字节码数组
     * @param <T> 类型
     * @return 反序列化后的类实例对象
     * @throws SerializeException
     */
    public static <T> T deserialize(Class<T> clazz,byte[] bytes) throws SerializeException {
        try {
            T instance = clazz.newInstance();
            Schema schema = RuntimeSchema.createFrom(clazz);
            ProtostuffIOUtil.mergeFrom(bytes, instance, schema);
            return instance;
        } catch (Exception  e) {
            throw new SerializeException(e);
        }

    }

    /**
     * 反序列化
     * @param instance 目标实例
     * @param bytes 序列化字节码数组
     * @param <T> 类型
     */
    public static <T> void deserialize(T instance,byte[] bytes){
        Schema schema = RuntimeSchema.createFrom(instance.getClass());
        ProtostuffIOUtil.mergeFrom(bytes, instance, schema);
    }
}
