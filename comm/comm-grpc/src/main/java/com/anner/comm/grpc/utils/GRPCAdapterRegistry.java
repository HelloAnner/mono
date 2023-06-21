package com.anner.comm.grpc.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import io.grpc.BindableService;
import io.grpc.Channel;

/**
 * grpc接口适配器注册类
 */
public class GRPCAdapterRegistry {
     private static final Map<Class<?>, Function<Channel, ?>> stubAdapterMap = new ConcurrentHashMap<>();

     private static final Map<Class<?>, Function<?, BindableService>> serviceAdapterMap = new ConcurrentHashMap<>();

     private GRPCAdapterRegistry() {
     }

     /**
      * 注册从grpc的stub到服务接口的适配器（用于客户端）
      * 
      * @param clazz       服务接口类
      * @param stubCreator 适配器 - grpc 生成
      */
     public static <T> void registerStubAdapter(Class<T> clazz, Function<Channel, T> stubCreator) {
          stubAdapterMap.put(clazz, stubCreator);
     }

     /**
      * 注册从业务服务接口/实现到grpc服务的适配器（用于服务端）
      * 
      * @param clazz          业务服务接口/实现类
      * @param serviceCreator 适配器 - grpc 生成
      */
     public static <T> void registerServiceAdapter(Class<T> clazz, Function<T, BindableService> serviceCreator) {
          serviceAdapterMap.put(clazz, serviceCreator);
     }

     /**
      * 将grpc的stub适配到服务接口 ，客户端调用，用于生成本地的调用对象
      * 
      * @param clazz   服务接口类
      * @param channel grpc的channel
      * @return 服务接口
      */
     @SuppressWarnings("unchecked")
     public static <T> T createStub(Class<T> clazz, Channel channel) {
          Function<Channel, ?> creator = stubAdapterMap.get(clazz);
          if (creator == null) {
               throw new UnsupportedOperationException("grpc stub creator not found for " + clazz);
          }
          return (T) creator.apply(channel);
     }

     /**
      * 将业务服务接口/实现适配到grpc服务 , 服务端调用 , 服务端启动统一将上述注册的服务添加到serverBuilder中
      * 
      * @param clazz 业务服务接口/实现类
      * @param svc   业务服务接口/实现
      * @return grpc服务
      */
     @SuppressWarnings("unchecked")
     public static <T> BindableService createService(Class<T> clazz, T svc) {
          Function<T, BindableService> creator = (Function<T, BindableService>) serviceAdapterMap.get(clazz);
          if (creator == null) {
               throw new UnsupportedOperationException("grpc service creator not found for " + clazz);
          }
          return creator.apply(svc);
     }
}
