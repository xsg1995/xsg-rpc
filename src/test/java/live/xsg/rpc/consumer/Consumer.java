package live.xsg.rpc.consumer;

import live.xsg.rpc.DemoService;
import live.xsg.rpc.proxy.RpcProxy;
import live.xsg.rpc.registry.Registry;
import live.xsg.rpc.registry.ServerInfo;
import live.xsg.rpc.registry.zookeeper.ZookeeperRegistry;

/**
 * Created by xsg on 2020/10/1.
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        Registry<ServerInfo> registry = new ZookeeperRegistry();
        registry.doOpen();
        DemoService demoService = RpcProxy.newInstance(DemoService.class, registry);
        String xsg = demoService.sayHello("xsg");
        System.out.println(xsg);
    }
}
