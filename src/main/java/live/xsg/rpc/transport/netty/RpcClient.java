package live.xsg.rpc.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Rpc 客户端
 * Created by xsg on 2020/10/1.
 */
public class RpcClient implements Closeable {

    private static final Map<String, RpcClient> RPC_CLIENT_MAP = new HashMap<>();
    private static final String SPLIT = "_";

    private Bootstrap clientBootstrap;
    private EventLoopGroup group;
    private String host;
    private int port;

    private RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.clientBootstrap = new Bootstrap();
        group = new NioEventLoopGroup(Math.min(Runtime.getRuntime().availableProcessors() + 1, 32));
        this.clientBootstrap.group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("rpc-encoder", new RpcEncoder());
                        socketChannel.pipeline().addLast("rpc-decoder", new RpcDecoder());
                        socketChannel.pipeline().addLast("client-handler", new RpcClientHandler());
                    }
                });
    }

    /**
     * 连接对应的主机与端口
     * @return ChannelFuture
     */
    public ChannelFuture connect() {
        ChannelFuture connect = this.clientBootstrap.connect(this.host, this.port);
        connect.awaitUninterruptibly();
        return connect;
    }

    @Override
    public void close() throws IOException {
        this.group.shutdownGracefully();
    }

    /**
     * 获取 RpcClient 实例
     * @param host 主机名
     * @param port 端口
     * @return RpcClient
     */
    public static RpcClient getRpcClient(String host, int port) {
        String key = host + SPLIT + port;
        RPC_CLIENT_MAP.putIfAbsent(key, new RpcClient(host, port));
        return RPC_CLIENT_MAP.get(key);
    }
}
