package io.github.kimmking.gateway.router;

import io.github.kimmking.gateway.router.excuter.RobinExecute;
import io.github.kimmking.gateway.router.pojo.SmoothServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 加权平滑轮询算法
 *
 * @author ahuxh
 */
public class WeightHttpEndpointRouter implements HttpEndpointRouter {

    @Override
    public String route(List<String> endpoints) {
        // 需要单例  列表 不能被垃圾回收
        RobinExecute robinExecute = RobinExecute.getInstance();
        robinExecute.getLock().lock();
        SmoothServer server = null;
        try {
            server = robinExecute.acquireWeightRoundRobinOfLock(endpoints);
        } catch (Exception e) {
            e.printStackTrace();
        } finally { //确保一定要释放锁
            robinExecute.getLock().unlock();
        }
        System.out.println("本次转发的服务器:" + server.getIp());
        return server.getIp();
    }



}
