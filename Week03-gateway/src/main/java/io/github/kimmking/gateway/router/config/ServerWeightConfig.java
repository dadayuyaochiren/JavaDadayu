package io.github.kimmking.gateway.router.config;

import java.util.HashMap;

/**
 * @author ahuxh
 * 权重配置
 */
public class ServerWeightConfig {

    /**
     *  待路由的Ip列表，Key代表Ip，Value代表该Ip的权重
     */
    public static HashMap<String, Integer> serverWeightMap = new HashMap<String, Integer>();

    static {
        // 配置每个地址的
        serverWeightMap.put("http://localhost:8801", 20);
        serverWeightMap.put("http://localhost:8802", 30);
        serverWeightMap.put("http://localhost:8803", 30);
    }


}
