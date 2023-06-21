package com.anner.comm.common.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.anner.comm.common.BaseCommServer;
import com.anner.comm.info.CommServiceInfo;
import com.anner.comm.utils.CommAnnotationUtils;

public class InternalCommServer extends BaseCommServer {

     private final Map<Class<?>, Object> serviceMap = new ConcurrentHashMap<>();

     @Override
     public String type() {
          return "internal";
     }

     @Override
     public void registerService(Object service) {
          CommServiceInfo info = CommAnnotationUtils.getServicesInfo(service.getClass());
          serviceMap.put(info.getClazz(), service);
     }

     /**
      * 根据接口获取对应的服务实现
      * 
      * @param clazz 接口类
      * @return 服务实现
      */
     public Object getService(Class<?> clazz) {
          return serviceMap.get(clazz);
     }

}
