package live.xsg.rpc.provider;

import live.xsg.rpc.BeanManager;
import live.xsg.rpc.DemoService;
import live.xsg.rpc.provider.impl.DemoServiceImpl;
import live.xsg.rpc.registry.Registry;
import live.xsg.rpc.registry.ServerInfo;
import live.xsg.rpc.registry.zookeeper.ZookeeperRegistry;
import live.xsg.rpc.transport.netty.RpcServer;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * Created by xsg on 2020/10/1.
 */
public class Provider {
    public static void main(String[] args) throws Exception {
        String serviceName = DemoService.class.getName();
        BeanManager.registerBean(serviceName, new DemoServiceImpl());

        Registry<ServerInfo> registry = new ZookeeperRegistry();
        registry.doOpen();
        ServiceInstance<ServerInfo> serviceInstance = ServiceInstance
                .<ServerInfo>builder()
                .name(serviceName)
                .payload(new ServerInfo("127.0.0.1", 20880))
                .build();
        registry.registerService(serviceInstance);

        RpcServer rpcServer = new RpcServer(20880);
        rpcServer.start();
    }
}
