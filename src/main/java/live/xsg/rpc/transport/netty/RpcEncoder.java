package live.xsg.rpc.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import live.xsg.rpc.compress.Compressor;
import live.xsg.rpc.compress.CompressorFactory;
import live.xsg.rpc.exchange.Header;
import live.xsg.rpc.exchange.Message;
import live.xsg.rpc.serialize.Serialization;
import live.xsg.rpc.serialize.SerializationFactory;

/**
 * 基于 netty 的编码器
 * Created by xsg on 2020/10/1.
 */
public class RpcEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        try {
            Header header = message.getHeader();
            byteBuf.writeShort(header.getMagic());
            byteBuf.writeByte(header.getVersion());
            byteBuf.writeByte(header.getExtraInfo());
            byteBuf.writeLong(header.getMessageId());

            if (Header.isHeartBeat(header.getExtraInfo())) {
                //心跳请求，无消息体
                byteBuf.writeInt(0);
                return;
            }
            Object body = message.getBody();
            //序列化实现
            Serialization serialization = SerializationFactory.get(header.getExtraInfo());
            //压缩与解压缩实现
            Compressor compressor = CompressorFactory.get(header.getExtraInfo());
            byte[] bodyBytes = serialization.serialize(body);
            byte[] payLoad = compressor.compress(bodyBytes);
            byteBuf.writeInt(payLoad.length);
            byteBuf.writeBytes(payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
