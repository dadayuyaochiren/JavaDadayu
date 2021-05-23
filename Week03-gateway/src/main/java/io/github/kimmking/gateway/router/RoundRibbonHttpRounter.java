package io.github.kimmking.gateway.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 轮询 负载均衡策略
 * @author ahuxh
 */
public class RoundRibbonHttpRounter implements HttpEndpointRouter{
    private static Logger logger = LoggerFactory.getLogger(RoundRibbonHttpRounter.class);
    // 初始化同步偏移量
    private static Integer pos = 0;

    @Override
    public String route(List<String> endpoints) {

        // 现在的问题就是,如何 及时的同步在线服务器的列表
        String server = null;
        // 这里 需要 同步锁
        synchronized (pos) {
           if (pos >= endpoints.size())
               pos = 0;
           server = endpoints.get(pos);
           pos ++;
        }
        System.out.println("本次转发的服务器:" + server);
        return server;

    }
}
