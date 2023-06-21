package com.anner.comm.common;

import java.lang.reflect.Proxy;

import com.anner.comm.info.CommProperties;
import com.anner.comm.info.CommServiceInfo;
import com.anner.comm.utils.CommAnnotationUtils;

/**
 * 代理类型的客户端。即通过java代理来实现方法的拦截，并转换为其他类型的调用方式
 */
public abstract class ProxiedCommClient extends BaseCommClient {

     public ProxiedCommClient(CommProperties properties) {
          super(properties);
     }

     /**
      * 子类中实现最终的服务调用方法
      * 
      * @param service 服务名称
      * @param method  方法名称
      * @param args    参数列表
      * @return 返回结果
      */
     protected abstract Object invoke(String service, String method, Object[] args, Class<?> returnType)
               throws Exception;

     @Override
     @SuppressWarnings("unchecked")
     public <T> T createCaller(Class<T> callerInterface) {
          CommServiceInfo info = CommAnnotationUtils.getServicesInfo(callerInterface);
          return (T) Proxy.newProxyInstance(callerInterface.getClassLoader(), new Class[] { callerInterface },
                    (proxt, method, args) -> {
                         if ("equals".equals(method.getName())) {
                              throw new UnsupportedOperationException();
                         }
                         return invoke(info.getName(), method.getName(), args, method.getReturnType());
                    });
     }
}
