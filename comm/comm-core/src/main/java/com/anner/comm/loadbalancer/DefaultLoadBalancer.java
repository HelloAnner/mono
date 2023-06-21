package com.anner.comm.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.anner.comm.common.CommClient;

/**
 * 负载均衡器默认实现
 */
public class DefaultLoadBalancer extends AbstractLoadBalancer {
     private static final String PEER_STATE_KEY = "DLB_PEER_STATE_KEY";

     private final List<CommClient> clientList = new ArrayList<>();

     /**
      * 添加一个客户端
      * 
      * @param client 客户端对象
      */
     public synchronized void addClient(CommClient client) {
          super.addClient(client);
          clientList.add(client);
          client.putAttribute(PEER_STATE_KEY, new PeerState());
     }

     /**
      * 删除一个客户端
      * 
      * @param identifier 客户端唯一标识
      * @return 返回被删除的客户端
      */
     public synchronized CommClient removeClient(String identifier) {
          CommClient client = super.removeClient(identifier);
          if (client != null) {
               clientList.remove(client);
          }
          return client;
     }

     @Override
     protected synchronized String selectClient() {
          if (clientList.isEmpty()) {
               throw new NullPointerException();
          }

          if (clientList.size() == 1) {
               return clientList.get(0).identifier();
          }

          // 选一个client，参考了nginx的轮询算法
          CommClient best = null;
          PeerState bestState = null;
          int total = 0;
          for (CommClient client : clientList) {
               PeerState state = client.getAttribute(PEER_STATE_KEY);

               state.currentWeight += client.weight();
               total += client.weight();

               if (bestState == null || state.currentWeight > bestState.currentWeight) {
                    best = client;
                    bestState = state;
               }
          }

          Optional.ofNullable(bestState).get().currentWeight -= total;

          return Optional.of(best).get().identifier();
     }

     private static class PeerState {
          int currentWeight;
     }

}
