package com.anner.comm.common;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.anner.comm.info.CommServiceInfo;
import com.anner.comm.utils.CommAnnotationUtils;

/**
 * 代理类型的服务端。将请求转化为对特定java类的反射调用。
 */
public abstract class ProxiedCommServer extends BaseCommServer {

     private final Map<String, ServiceEntry> serviceMap = new ConcurrentHashMap<>();

     @Override
     public void registerService(Object service) {
          CommServiceInfo info = CommAnnotationUtils.getServicesInfo(service.getClass());
          serviceMap.put(info.getName(), new ServiceEntry(service, info));
     }

     /**
      * 反射执行服务对象中的方法，用于子类中调用
      * 
      * @param service 服务名称
      * @param method  方法名称
      * @param args    对应java方法的参数列表
      * @return 返回结果
      */
     protected Object handle(String service, String method, Object[] args) throws Exception {
          ServiceEntry entry = serviceMap.get(service);
          if (entry == null) {
               throw new IllegalArgumentException(String.format("service %s not found", service));
          }
          return entry.call(method, args);
     }

     private static class ServiceEntry {
          private final Object service;
          private final Map<String, Method> methodMap;

          public ServiceEntry(Object service, CommServiceInfo info) {
               this.service = service;
               methodMap = info.getMethodMap();
          }

          public Object call(String methodName, Object[] args) throws Exception {
               Method method = methodMap.get(methodName);
               if (method == null) {
                    throw new NoSuchMethodException(String.format("%s in class %s", methodName, service.getClass()));
               }
               return method.invoke(service, args);
          }
     }
}
