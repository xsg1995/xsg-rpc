package live.xsg.rpc.serialize;

import live.xsg.rpc.exchange.Header;
import live.xsg.rpc.serialize.hessian.HessianSerialization;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取序列化具体实现的工厂类
 * Created by xsg on 2020/10/1.
 */
public class SerializationFactory {

    private static Map<Byte, Serialization> serializationMap = new HashMap<>();

    static {
        serializationMap.put(HessianSerialization.HESSIAN_BYTE, new HessianSerialization());
    }

    /**
     * 获取具体的序列化实现
     * @param extraInfo 请求附加信息
     * @return 序列化实现
     */
    public static Serialization get(byte extraInfo) {
        byte flag = (byte) (Header.SERIALIZATION_BYTE & extraInfo);
        Serialization serialization = serializationMap.get(flag);
        if (serialization == null)
            throw new IllegalArgumentException("serialization is null，flag = " + flag);
        return serialization;
    }

}
