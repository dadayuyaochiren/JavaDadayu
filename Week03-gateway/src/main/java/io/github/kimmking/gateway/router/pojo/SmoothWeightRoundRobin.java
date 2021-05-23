package io.github.kimmking.gateway.router.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 平滑权重算法
 *     1. 每一个回合, 取当前最大的权重  减去 (Σ weight)
 *     2. 每一个回合, 各自自增自身权重
 *     重复此过程 即可
 * @author ahuxh
 */
public class SmoothWeightRoundRobin {

    /**初始化所有的服务器**/
    List<SmoothServer> servers = new ArrayList<>();

    /**服务器权重总和*/
    private int weightCount;

    public void init(List<SmoothServer> servers) {
        this.servers = servers;
        // reduce (合并)
        this.weightCount = this.servers.stream().map(server -> server.getWeight()).reduce(0, (l, r) -> l + r);
    }

    /**获取需要执行的服务器**/
    public SmoothServer getServer() {
        SmoothServer tmpSv = null;
        // 过程一
        for (SmoothServer sv : servers) {
            sv.setCurWeight(sv.getWeight() + sv.getCurWeight());
            if (tmpSv == null || tmpSv.getCurWeight() < sv.getCurWeight()) tmpSv = sv;
        }
        // 过程 二
        tmpSv.setCurWeight(tmpSv.getCurWeight() - weightCount);
        return tmpSv;
    }

}