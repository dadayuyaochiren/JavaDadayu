package io.github.kimmking.gateway.router;

import io.github.kimmking.gateway.inbound.HttpInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class RandomHttpEndpointRouter implements HttpEndpointRouter {
    private static Logger logger = LoggerFactory.getLogger(RandomHttpEndpointRouter.class);

    @Override
    public String route(List<String> urls) {
        int size = urls.size();
        // 获得随机类
        Random random = new Random(System.currentTimeMillis());
        String server = urls.get(random.nextInt(size));
        System.out.println("本次转发的服务器:" + server);
        return server;
    }
}
