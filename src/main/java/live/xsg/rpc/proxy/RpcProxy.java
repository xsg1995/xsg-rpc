package live.xsg.rpc.proxy;

import live.xsg.rpc.exchange.Header;
import live.xsg.rpc.exchange.Message;
import live.xsg.rpc.exchange.Request;
import live.xsg.rpc.registry.Registry;
import live.xsg.rpc.registry.ServerInfo;
import live.xsg.rpc.transport.RpcMessageSender;
import live.xsg.rpc.transport.netty.RpcClient;
import org.apache.curator.x.discovery.ServiceInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * rpc 代理对象
 * Created by xsg on 2020/10/1.
 */
public class RpcProxy implements InvocationHandler {

    //服务的接口名称
    private String serviceName;
    //注册中心
    private Registry<ServerInfo> registry;

    public RpcProxy(String serviceName, Registry<ServerInfo> registry) {
        this.serviceName = serviceName;
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz, Registry<ServerInfo> registry) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz}, new RpcProxy(clazz.getName(), registry));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<ServiceInstance<ServerInfo>> serviceInstances = registry.queryForInstances(this.serviceName);
        ServiceInstance<ServerInfo> serviceInstance = serviceInstances.get(ThreadLocalRandom.current().nextInt(serviceInstances.size()));

        String methodName = method.getName();
        Class<?>[] argTypes = method.getParameterTypes();
        //构造 request
        Request request = new Request(this.serviceName, methodName, argTypes, args);
        //构造 header
        Header header = Header.getDefaultRequestHeader();
        //构造请求
        Message<Request> message = new Message<>(header, request);

        return rpc(serviceInstance.getPayload(), message);
    }

    /**
     * 远程调用
     *
     * @param serverInfo 远程调用的服务
     * @param message    消息
     * @return 返回远程调用的结果
     */
    private Object rpc(ServerInfo serverInfo, Message<Request> message) {
        if (serverInfo == null)
            throw new RuntimeException("not serverInfo error");

        RpcMessageSender rpcMessageSender = new RpcMessageSender(RpcClient.getRpcClient(serverInfo.getHost(), serverInfo.getPort()));
        return rpcMessageSender.send(message);
    }
}
