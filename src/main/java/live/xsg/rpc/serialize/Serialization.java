package live.xsg.rpc.serialize;

import java.io.IOException;

/**
 * 序列化接口
 * Created by xsg on 2020/10/1.
 */
public interface Serialization {

    /**
     * 对象序列化为 byte[] 数组
     * @param obj 序列化的对象
     * @param <T> 序列化的对象类型
     * @return 序列化后的结果
     * @throws IOException 异常
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * byte[] 数组反序列化为对象
     * @param data 反序列化的数组
     * @param clazz 反序列化后的类型
     * @param <T> 反序列化后的类型
     * @return 反序列化后的结果
     * @throws IOException 异常
     */
    <T> T deSerialize(byte[] data, Class<T> clazz) throws IOException;
}
