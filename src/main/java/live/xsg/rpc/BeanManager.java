package live.xsg.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理 bean
 * Created by xsg on 2020/10/1.
 */
public class BeanManager {

    //存储注册的 bean
    private static final Map<String, Object> BEANS = new ConcurrentHashMap<>();

    /**
     * 根据 bean 的名称获取 bean
     * @param serviceName bean 的名称
     * @return bean 对象
     */
    public static Object getBean(String serviceName) {
        Object o = BEANS.get(serviceName);
        if (o == null) {
            throw new RuntimeException("not such bean, serviceName = " + serviceName);
        }
        return o;
    }

    /**
     * 注册 bean
     * @param beanName bean的名称
     * @param bean bean的实例
     */
    public static void registerBean(String beanName, Object bean) {
        BEANS.put(beanName, bean);
    }
}
