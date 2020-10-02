package live.xsg.rpc.serialize.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import live.xsg.rpc.serialize.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 基于 hessian 实现序列化与反序列化
 * Created by xsg on 2020/10/1.
 */
public class HessianSerialization implements Serialization {

    //hessian 标识位
    public static byte HESSIAN_BYTE = 0x2;

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(bos);
        hessianOutput.writeObject(obj);
        return bos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deSerialize(byte[] data, Class<T> clazz) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        HessianInput hessianInput = new HessianInput(bis);
        return (T) hessianInput.readObject();
    }
}
