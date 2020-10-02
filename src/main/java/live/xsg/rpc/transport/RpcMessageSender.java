package live.xsg.rpc.transport;

import io.netty.channel.ChannelFuture;
import live.xsg.rpc.exception.RpcException;
import live.xsg.rpc.exchange.Message;
import live.xsg.rpc.exchange.Request;
import live.xsg.rpc.exchange.ResponseFuture;
import live.xsg.rpc.transport.netty.RpcClient;

/**
 * 远程消息发送
 * Created by xsg on 2020/10/1.
 */
public class RpcMessageSender {

    private ChannelFuture channelFuture;

    public RpcMessageSender(RpcClient rpcClient) {
        this.channelFuture = rpcClient.connect();
    }

    /**
     * 发送远程消息
     * @param message 要发送的消息
     * @return 返回结果
     */
    public Object send(Message<Request> message) {
        Long messageId = message.getHeader().getMessageId();
        ResponseFuture responseFuture = new ResponseFuture(messageId);

        channelFuture.channel().writeAndFlush(message);

        Object result = responseFuture.get();
        if (result instanceof RpcException) {
            return ((RpcException) result).getCause();
        }

        return result;
    }
}
