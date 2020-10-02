package live.xsg.rpc.transport.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Rpc 服务端
 * Created by xsg on 2020/10/1.
 */
public class RpcServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    protected int port;

    public RpcServer(int port) {
        this.port = port;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(Math.min(Runtime.getRuntime().availableProcessors() + 1, 32));
        this.serverBootstrap = new ServerBootstrap().group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("rpc-decoder", new RpcDecoder());
                        socketChannel.pipeline().addLast("rpc-encoder", new RpcEncoder());
                        socketChannel.pipeline().addLast("server-handler", new RpcServerHandler());
                    }
                });
    }

    /**
     * 开始服务端端口
     */
    public ChannelFuture start() {
        ChannelFuture channelFuture = this.serverBootstrap.bind(this.port);
        Channel channel = channelFuture.channel();
        channel.closeFuture();
        return channelFuture;
    }
}
