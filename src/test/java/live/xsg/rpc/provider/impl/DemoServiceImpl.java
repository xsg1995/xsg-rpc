package live.xsg.rpc.provider.impl;

import live.xsg.rpc.DemoService;

/**
 * Created by xsg on 2020/10/1.
 */
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return String.format("Hello, %s!", name);
    }
}
