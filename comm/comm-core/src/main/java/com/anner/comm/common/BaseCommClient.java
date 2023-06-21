package com.anner.comm.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.anner.comm.info.CommProperties;

public abstract class BaseCommClient implements CommClient {

     private CommProperties properties;

     // 存储其他的属性
     private final Map<String, Object> attrMap = new ConcurrentHashMap<>();

     public BaseCommClient(CommProperties properties) {
          this.properties = properties;
     }

     @Override
     public int weight() {
          return properties.getAsInt(CommProperties.WEIGHT, 1);
     }

     @Override
     public void putAttribute(String key, Object value) {
          attrMap.put(key, value);
     }

     @Override
     @SuppressWarnings("unchecked")
     public <T> T getAttribute(String key) {
          return (T) attrMap.get(key);
     }

     @Override
     public String identifier() {
          return properties.getAsString(CommProperties.IDENTIFIER, "");
     }
}
