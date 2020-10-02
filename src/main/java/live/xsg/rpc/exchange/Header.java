package live.xsg.rpc.exchange;

import live.xsg.rpc.compress.snappy.SnappyCompressor;
import live.xsg.rpc.serialize.hessian.HessianSerialization;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 消息头
 * Created by xsg on 2020/10/1.
 */
public class Header {

    //请求消息id生成器
    private static transient AtomicLong MESSAGE_ID_GENERATOR = new AtomicLong();

    //消息头为 16 字节
    public static int HEADER_SIZE = 16;
    //默认版本
    public static byte VERSION = 1;
    //魔数
    public static short MAGIC = (short) 0xdabb;
    //心跳请求位标识 第0位
    public static byte HEART_BEAT_BYTE = 0x1;
    //序列化位标识 第1,2位
    public static byte SERIALIZATION_BYTE = 0x6;
    //压缩算法位标识 第3,4位
    public static byte COMPRESSOR_BYTE = 0x18;
    //是否是请求表示，第5位 为1，则为请求
    public static byte REQUEST_BYTE = 0x20;

    //魔数
    private short magic; // 2 字节
    //协议版本
    private byte version;  // 1 字节
    //附加信息
    private byte extraInfo; // 1 字节
    //消息id
    private Long messageId; // 8 字节
    //消息体长度
    private Integer size; // 4 字节

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(byte extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * 判断请求是否为心跳请求
     * @param extraInfo 请求附加信息
     * @return true or false
     */
    public static boolean isHeartBeat(byte extraInfo) {
        //extraInfo最低位为1，则为心跳请求
        return (extraInfo & HEART_BEAT_BYTE) == HEART_BEAT_BYTE;
    }

    /**
     * 判断消息是否是请求消息
     * @param extraInfo 请求附加信息
     * @return true or false
     */
    public static boolean isRequest(byte extraInfo) {
        int requestByte = extraInfo & Header.REQUEST_BYTE;
        return requestByte == Header.REQUEST_BYTE;
    }

    /**
     * 获取 request 的 Header 的基础默认信息
     * @return 默认 header
     */
    public static Header getDefaultRequestHeader() {
        Header header = new Header();
        header.setMessageId(generatorMessageId());
        header.setMagic(Header.MAGIC);
        header.setVersion(Header.VERSION);
        byte extraInfo = (byte) (HessianSerialization.HESSIAN_BYTE | SnappyCompressor.SNAPPY_BYTE | Header.REQUEST_BYTE);
        header.setExtraInfo(extraInfo);
        return header;
    }

    /**
     * 获取 response 的 Header 的基础默认信息
     * @param messageId 消息id
     * @return 默认 header
     */
    public static Header getDefaultResponseHeader(long messageId) {
        Header header = new Header();
        header.setMessageId(messageId);
        header.setMagic(Header.MAGIC);
        header.setVersion(Header.VERSION);
        byte extraInfo = (byte) (HessianSerialization.HESSIAN_BYTE | SnappyCompressor.SNAPPY_BYTE);
        header.setExtraInfo(extraInfo);
        return header;
    }

    /**
     * 生成消息id
     * @return 消息id
     */
    public static long generatorMessageId() {
        return MESSAGE_ID_GENERATOR.incrementAndGet();
    }

}
