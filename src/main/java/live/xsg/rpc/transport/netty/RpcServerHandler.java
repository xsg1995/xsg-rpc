package live.xsg.rpc.transport.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import live.xsg.rpc.exchange.Header;
import live.xsg.rpc.exchange.Message;
import live.xsg.rpc.exchange.Request;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 服务端处理器
 * Created by xsg on 2020/10/1.
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<Message<Request>> {

    //业务线程池
    private static Executor executor = Executors.newCachedThreadPool();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message<Request> message) throws Exception {
        byte extraInfo = message.getHeader().getExtraInfo();
        if (Header.isHeartBeat(extraInfo)) {
            //心跳请求，直接返回
            channelHandlerContext.writeAndFlush(message);
            return;
        }
        executor.execute(new InvokeRunnable(message, channelHandlerContext));
    }
}
