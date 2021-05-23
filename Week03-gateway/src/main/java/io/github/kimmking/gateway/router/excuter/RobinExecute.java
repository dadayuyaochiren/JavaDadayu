package io.github.kimmking.gateway.router.excuter;

import io.github.kimmking.gateway.router.config.ServerWeightConfig;
import io.github.kimmking.gateway.router.pojo.SmoothServer;
import io.github.kimmking.gateway.router.pojo.SmoothWeightRoundRobin;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author ahuxh
 */
public class RobinExecute {

    // 不让通过 构造器
    private RobinExecute(){}
    private static RobinExecute robinExecute = new RobinExecute();
    public static RobinExecute getInstance(){
        return robinExecute;
    }

    /** 线程使用完不会清除该变量,会一直保留着，由于线程 池化所以不用担心内存泄漏 **/
    private ThreadLocal<SmoothWeightRoundRobin> weightRoundRobinTl = new ThreadLocal<SmoothWeightRoundRobin>();

    // 同步锁
    private ReentrantLock lock = new ReentrantLock();

    /** 为什么添加volatile，是因为 ReentrantLock 并不保证内存可见性 **/
    private volatile SmoothWeightRoundRobin smoothWeightRoundRobin;


    /**
     * 在分布式情况，第二种使用方式  使用cas ReentrantLock 可重入锁
     * notice:
     * @return
     */
    public SmoothServer acquireWeightRoundRobinOfLock(List<String> endpoints) {
        if (smoothWeightRoundRobin == null) {
            SmoothWeightRoundRobin weightRoundRobin = new SmoothWeightRoundRobin();
            List<SmoothServer> servers = new ArrayList<>();
            // 从传入的拿
            endpoints.forEach(e -> {
                servers.add(new SmoothServer(e, ServerWeightConfig.serverWeightMap.getOrDefault(e,10)));
            });
            // 初始化
            weightRoundRobin.init(servers);
            smoothWeightRoundRobin = weightRoundRobin;
        }
        return smoothWeightRoundRobin.getServer();
    }

    /**
     * 在分布式情况，第一种使用方式  ThreadLock
     * notice: 只有在使用池化技术的情况才建议使用此方式，否则达不到效果，还会造成内存泄漏
     * @return
     */
    public SmoothWeightRoundRobin acquireWeightRoundRobinOfTheadLocal(List<String> endpoints) {
        return Optional.ofNullable(weightRoundRobinTl.get())
            .orElseGet(() -> {
                SmoothWeightRoundRobin weightRoundRobin = new SmoothWeightRoundRobin();
                List<SmoothServer> servers = new ArrayList<>();
                // 从传入的拿
                endpoints.forEach(e -> {
                    servers.add(new SmoothServer(e, ServerWeightConfig.serverWeightMap.getOrDefault(e,10)));
                });
                weightRoundRobin.init(servers);
                weightRoundRobinTl.set(weightRoundRobin);
                return weightRoundRobin;
            });
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public ThreadLocal<SmoothWeightRoundRobin> getWeightRoundRobinTl() {
        return weightRoundRobinTl;
    }
}