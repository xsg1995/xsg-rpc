package live.xsg.rpc.transport.netty;

import io.netty.channel.ChannelHandlerContext;
import live.xsg.rpc.BeanManager;
import live.xsg.rpc.compress.snappy.SnappyCompressor;
import live.xsg.rpc.exception.RpcException;
import live.xsg.rpc.exchange.Header;
import live.xsg.rpc.exchange.Message;
import live.xsg.rpc.exchange.Request;
import live.xsg.rpc.exchange.Response;
import live.xsg.rpc.serialize.hessian.HessianSerialization;

import java.lang.reflect.Method;

/**
 * Created by xsg on 2020/10/1.
 */
public class InvokeRunnable implements Runnable {

    private Message<Request> message;
    private ChannelHandlerContext ctx;

    public InvokeRunnable(Message<Request> message, ChannelHandlerContext ctx) {
        this.message = message;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        Long messageId = message.getHeader().getMessageId();
        Header header = Header.getDefaultResponseHeader(messageId);

        Response response = new Response();
        try {
            Request request = message.getBody();
            String serviceName = request.getServiceName();
            String methodName = request.getMethodName();
            Class[] argTypes = request.getArgTypes();
            Object[] args = request.getArgs();

            Object bean = BeanManager.getBean(serviceName);
            Method method = bean.getClass().getMethod(methodName, argTypes);
            Object result = method.invoke(bean, args);
            response.setResult(result);
        } catch (Exception e) {
            response.setResultCode(Response.ERROR_CODE);
            response.setResult(new RpcException(e));
            e.printStackTrace();
        }

        ctx.writeAndFlush(new Message<>(header, response));
    }
}
