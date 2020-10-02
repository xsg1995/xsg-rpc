package live.xsg.rpc.registry.zookeeper;

import live.xsg.rpc.registry.Registry;
import live.xsg.rpc.registry.ServerInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * zookeeper 实现注册中心
 * Created by xsg on 2020/10/1.
 */
public class ZookeeperRegistry implements Registry<ServerInfo> {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final String ROOT_PATH = "/xsg/rpc";

    private InstanceSerializer<ServerInfo> serializer = new JsonInstanceSerializer<>(ServerInfo.class);
    private ServiceDiscovery<ServerInfo> serviceDiscovery;
    private ServiceCache<ServerInfo> serviceCache;

    @Override
    public void doOpen() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new ExponentialBackoffRetry(1000, 3));
        client.start();
        client.blockUntilConnected();
        client.createContainers(ROOT_PATH);
        this.serviceDiscovery = ServiceDiscoveryBuilder
                .builder(ServerInfo.class)
                .client(client)
                .basePath(ROOT_PATH)
                .serializer(serializer)
                .build();
        this.serviceDiscovery.start();
    }

    @Override
    public void registerService(ServiceInstance<ServerInfo> serviceInstance) throws Exception {
        this.serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unregisterService(ServiceInstance<ServerInfo> serviceInstance) throws Exception {
        this.serviceDiscovery.unregisterService(serviceInstance);
    }

    @Override
    public List<ServiceInstance<ServerInfo>> queryForInstances(String serviceName) throws Exception {

        if (this.serviceCache == null) {
            this.serviceCache = this.serviceDiscovery.serviceCacheBuilder()
                    .name(serviceName)
                    .build();
            serviceCache.start();
        }

        return this.serviceCache.getInstances().stream()
                .filter(s -> s.getName().equals(serviceName))
                .collect(Collectors.toList());
    }

}
