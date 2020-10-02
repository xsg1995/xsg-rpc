package live.xsg.rpc.compress;

import java.io.IOException;

/**
 * 数据压缩器
 * Created by xsg on 2020/10/1.
 */
public interface Compressor {

    /**
     * 对字节数组进行压缩
     * @param array 压缩前的字节数组
     * @return 压缩后的字节数组
     * @throws IOException 异常
     */
    byte[] compress(byte[] array) throws IOException;

    /**
     * 对压缩后的字节数组进行解压缩
     * @param array 压缩后的字节数组
     * @return 压缩前的字节数组
     * @throws IOException
     */
    byte[] unCompress(byte[] array) throws IOException;
}
