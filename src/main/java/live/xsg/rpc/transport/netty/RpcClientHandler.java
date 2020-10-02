package live.xsg.rpc.transport.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import live.xsg.rpc.exchange.*;

/**
 * 客户端处理器
 * Created by xsg on 2020/10/1.
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<Message<Response>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message<Response> message) throws Exception {
        Header header = message.getHeader();
        ResponseFuture responseFuture = ResponseFuture.get(header.getMessageId());
        Response response = message.getBody();
        if (response == null && Header.isHeartBeat(header.getExtraInfo())) {
            //心跳请求响应
            response = new Response();
            response.setResultCode(Response.HEARTBEAT_CODE);
        }
        responseFuture.received(response);
    }
}
