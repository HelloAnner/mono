package com.anner.comm;

import com.anner.comm.loadbalancer.CommLoadBalancer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微服务调用入口类（客户端）
 */
public class CommRepo {

    private static final Map<String, CommLoadBalancer> loadBalancerMap = new ConcurrentHashMap<>();

    /**
     * 注册指定域的负载均衡器
     *
     * @param domain       域，表示一类业务的调用接口，如报表域、平台域等
     * @param loadBalancer 负载均衡器
     */
    public static void registerLoadBalancer(String domain, CommLoadBalancer loadBalancer) {
        loadBalancerMap.put(domain, loadBalancer);
    }

    /**
     * 获取客户端调用接口
     *
     * @param domain          域，表示一类业务的调用接口，如报表域、平台域等
     * @param callerInterface 客户端调用接口
     * @return 接口实例
     */
    public static <T> T getCaller(String domain, Class<T> callerInterface) {
        CommLoadBalancer lb = loadBalancerMap.get(domain);
        if (lb == null) {
            throw new RuntimeException("domain " + domain + " not found");
        }
        return lb.getCaller(callerInterface);
    }
}
