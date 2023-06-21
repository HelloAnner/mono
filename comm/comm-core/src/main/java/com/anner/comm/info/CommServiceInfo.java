package com.anner.comm.info;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 单个微服务接口的注册信息
 */
public class CommServiceInfo {

     private String name;

     private Class<?> clazz;

     private Method[] methods;

     public CommServiceInfo(String name, Class<?> clazz, Method[] methods) {
          this.name = name;
          this.clazz = clazz;
          this.methods = methods;
     }

     public Class<?> getClazz() {
          return clazz;
     }

     public String getName() {
          return name;
     }

     public Method[] getMethods() {
          return methods;
     }

     public Map<String, Method> getMethodMap() {
          return Arrays.stream(getMethods()).collect(Collectors.toConcurrentMap(Method::getName, m -> m));
     }
}
