package live.xsg.rpc.compress.snappy;

import live.xsg.rpc.compress.Compressor;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * snappy 实现压缩与解压缩
 * Created by xsg on 2020/10/1.
 */
public class SnappyCompressor implements Compressor {

    //snappy 标识位
    public static byte SNAPPY_BYTE = 0x8;

    @Override
    public byte[] compress(byte[] array) throws IOException {
        if (array == null) return null;

        return Snappy.compress(array);
    }

    @Override
    public byte[] unCompress(byte[] array) throws IOException {
        if (array == null) return null;

        return Snappy.uncompress(array);
    }
}
