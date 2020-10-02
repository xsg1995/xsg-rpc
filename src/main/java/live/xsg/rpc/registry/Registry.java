package live.xsg.rpc.registry;

import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;

/**
 * 注册中心
 * Created by xsg on 2020/10/1.
 */
public interface Registry<T> {

    void registerService(ServiceInstance<T> serviceInstance) throws Exception;

    void unregisterService(ServiceInstance<T> serviceInstance) throws Exception;

    List<ServiceInstance<T>> queryForInstances(String serviceName) throws Exception;

    void doOpen() throws Exception;
}
