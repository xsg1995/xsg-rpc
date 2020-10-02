package live.xsg.rpc.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import live.xsg.rpc.compress.Compressor;
import live.xsg.rpc.compress.CompressorFactory;
import live.xsg.rpc.exchange.Header;
import live.xsg.rpc.exchange.Message;
import live.xsg.rpc.exchange.Request;
import live.xsg.rpc.exchange.Response;
import live.xsg.rpc.serialize.Serialization;
import live.xsg.rpc.serialize.SerializationFactory;

import java.util.List;

/**
 * 基于 netty 的解码器
 * Created by xsg on 2020/10/1.
 */
public class RpcDecoder extends ByteToMessageDecoder {

    @SuppressWarnings("unchecked")
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < Header.HEADER_SIZE) {
            return;
        }

        byteBuf.markReaderIndex();
        //魔数
        short magic = byteBuf.readShort();
        if (magic != Header.MAGIC) {
            byteBuf.resetReaderIndex();
            throw new RuntimeException("magic number error:" + magic);
        }
        byte version = byteBuf.readByte();
        byte extraInfo = byteBuf.readByte();
        long messageId = byteBuf.readLong();
        int size = byteBuf.readInt();

        //封装消息头
        Header header = new Header();
        header.setMagic(magic);
        header.setVersion(version);
        header.setExtraInfo(extraInfo);
        header.setMessageId(messageId);
        header.setSize(size);

        if (!Header.isHeartBeat(extraInfo)) {
            //非心跳请求，获取消息体
            if (byteBuf.readableBytes() < size) {
                //没有读取到足够的数据
                byteBuf.resetReaderIndex();
                return;
            }

            //消息体
            byte[] payLoad = new byte[size];
            byteBuf.readBytes(payLoad);

            //获取序列化与压缩方式的实现
            Serialization serialization = SerializationFactory.get(extraInfo);
            Compressor compressor = CompressorFactory.get(extraInfo);

            byte[] unCompressPayLoad = compressor.unCompress(payLoad);
            if (Header.isRequest(extraInfo)) {
                Request request = serialization.deSerialize(unCompressPayLoad, Request.class);
                //具体的消息
                Message<Request> message = new Message<>(header, request);
                list.add(message);
            } else {
                Response response = serialization.deSerialize(unCompressPayLoad, Response.class);
                //具体的消息
                Message<Response> message = new Message<>(header, response);
                list.add(message);
            }
        }

    }
}
