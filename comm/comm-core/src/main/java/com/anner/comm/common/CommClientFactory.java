package com.anner.comm.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.anner.comm.info.CommProperties;

/*
 * 通信客户端工厂
 */
public class CommClientFactory {
     private static final Map<String, Function<CommProperties, CommClient>> clientCreatorMap = new ConcurrentHashMap<>();

     private CommClientFactory() {
     }

     public static void registerClient(String type, Function<CommProperties, CommClient> creator) {
          clientCreatorMap.put(type, creator);
     }

     /**
      * 根据配置属性创建通信客户端实例
      * 
      * @param properties 配置属性
      * @return 客户端实例
      */
     public static CommClient create(CommProperties commProperties) {
          String type = commProperties.getAsString(CommProperties.TYPE, "");
          // 自动生成一个客户端
          if (!commProperties.containsKey(CommProperties.IDENTIFIER)) {
               commProperties.put(CommProperties.IDENTIFIER, generateIdentifier(commProperties));
          }
          Function<CommProperties, CommClient> creator = clientCreatorMap.get(type);

          if (creator == null) {
               throw new UnsupportedOperationException("creator is not exist");
          }
          return creator.apply(commProperties);
     }

     /**
      * 生成客户端的唯一标识
      * 
      * @param properties 配置属性
      * @return 唯一标识
      */
     public static String generateIdentifier(CommProperties properties) {
          // 通过协议、主机、端口、路径等信息来生成
          return properties.get(CommProperties.TYPE, "*") +
                    "://" +
                    properties.get(CommProperties.HOST, "*") +
                    ":" +
                    properties.get(CommProperties.PORT, "*") +
                    properties.get("path", "");
     }
}
