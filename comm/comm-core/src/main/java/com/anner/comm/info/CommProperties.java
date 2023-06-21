package com.anner.comm.info;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置属性
 */
public class CommProperties implements Cloneable {
     public static final String KIND = "kind";
     public static final String DOMAIN = "domain";
     public static final String TYPE = "type";
     public static final String HOST = "host";
     public static final String PORT = "port";
     public static final String WEIGHT = "weight";
     public static final String IDENTIFIER = "identifier";

     private Map<String, Object> propertiesMap = new ConcurrentHashMap<>();

     public void put(String key, Object value) {
          propertiesMap.put(key, value);
     }

     public void put(CommAttribute attr) {
          put(attr.getKey(), attr.getValue());
     }

     public boolean containsKey(String key) {
          return propertiesMap.containsKey(key);
     }

     public Object get(String key) {
          return propertiesMap.get(key);
     }

     public Object get(String key, Object defaultValue) {
          Object t = get(key);
          return t == null ? defaultValue : t;
     }

     public int getAsInt(String key, int defaultValue) {
          Object o = get(key);
          if (o == null) {
               return defaultValue;
          }
          if (o instanceof Integer) {
               return (int) o;
          }
          try {
               return Integer.valueOf(o.toString());
          } catch (NumberFormatException e) {
               return defaultValue;
          }
     }

     public String getAsString(String key, String defaultValue) {
          Object o = get(key);
          if (o == null) {
               return defaultValue;
          }
          return o.toString();
     }

     public boolean getAsBool(String key) {
          Object o = get(key);
          if (o == null) {
               return false;
          }
          if (o instanceof Boolean) {
               return (boolean) o;
          }
          return Boolean.parseBoolean(o.toString());
     }

     public Map<String, Object> toMap() {
          return new HashMap<>(propertiesMap);
     }

     @Override
     public CommProperties clone() throws CloneNotSupportedException {
          CommProperties commProperties = (CommProperties) super.clone();
          commProperties.propertiesMap = new ConcurrentHashMap<>(propertiesMap);
          return commProperties;
     }

}
