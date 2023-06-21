package com.anner.comm.loadbalancer;

/**
 * 负载均衡
 */
public interface CommLoadBalancer {

     <T> T getCaller(Class<T> callerInterface);
}
