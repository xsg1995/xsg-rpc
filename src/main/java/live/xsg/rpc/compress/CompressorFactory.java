package live.xsg.rpc.compress;

import live.xsg.rpc.compress.snappy.SnappyCompressor;
import live.xsg.rpc.exchange.Header;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取压缩与解压缩实现类工厂
 * Created by xsg on 2020/10/1.
 */
public class CompressorFactory {

    private static Map<Byte, Compressor> compressorMap = new HashMap<>();

    static {
        compressorMap.put(SnappyCompressor.SNAPPY_BYTE, new SnappyCompressor());
    }

    /**
     * 获取具体的压缩与解压缩实现类
     * @param extraInfo 请求附加信息
     * @return 具体的压缩与解压缩实现
     */
    public static Compressor get(byte extraInfo) {
        byte flag = (byte) (extraInfo & Header.COMPRESSOR_BYTE);
        Compressor compressor = compressorMap.get(flag);
        if (compressor == null)
            throw new IllegalArgumentException("compressor is null，flag = " + flag);
        return compressor;
    }
}
