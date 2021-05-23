package io.github.kimmking.gateway.router.pojo;

/**
 * 平滑权重
 *  单一 抽象
 * @author ahuxh
 */
public class SmoothServer {

    public SmoothServer(String ip, int weight) {
        this.ip = ip;
        this.weight = weight;
    }

    public SmoothServer(String ip, int weight, int curWeight) {
        this.ip = ip;
        this.weight = weight;
        this.curWeight = curWeight;
    }

    private String ip;

    private int weight;

    private int curWeight;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getCurWeight() {
        return curWeight;
    }

    public void setCurWeight(int curWeight) {
        this.curWeight = curWeight;
    }
}