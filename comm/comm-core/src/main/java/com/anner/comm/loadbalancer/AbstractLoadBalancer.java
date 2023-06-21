package com.anner.comm.loadbalancer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.anner.comm.common.CommClient;

public abstract class AbstractLoadBalancer implements CommLoadBalancer {
     private final Map<String, CommClient> clientMap = new ConcurrentHashMap<>();

     private final Map<String, Map<Class<?>, Object>> cachedCallerMap = new ConcurrentHashMap<>();

     /**
      * 通过负载均衡策略选出一个即将被调用的客户端
      * 
      * @return 客户端唯一标识
      */
     protected abstract String selectClient();

     @Override
     @SuppressWarnings("unchecked")
     public <T> T getCaller(Class<T> callerInterface) {
          String id = selectClient();
          Map<Class<?>, Object> cMap = cachedCallerMap.computeIfAbsent(id, k -> new ConcurrentHashMap<>());
          return (T) cMap.computeIfAbsent(callerInterface, iface -> createCaller(id, iface));
     }

     /**
      * 添加一个客户端
      * 
      * @param client 客户端对象
      */
     public synchronized void addClient(CommClient client) {
          if (clientMap.containsKey(client.identifier())) {
               throw new IllegalArgumentException("client already exist");
          }
          clientMap.put(client.identifier(), client);
     }

     /**
      * 通知客户端已经被删掉
      * 
      * @param identifier 客户端唯一标识
      */
     public synchronized CommClient removeClient(String identifier) {
          CommClient client = clientMap.remove(identifier);
          cachedCallerMap.remove(identifier);
          return client;
     }

     private synchronized Object createCaller(String identifier, Class<?> callerInterface) {
          return clientMap.get(identifier).createCaller(callerInterface);
     }

}
