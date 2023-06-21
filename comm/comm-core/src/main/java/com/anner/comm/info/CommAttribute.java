package com.anner.comm.info;

/**
 * 用于服务发布的消息
 */
public class CommAttribute {

     private String key;

     private Object value;

     public CommAttribute(String key, Object value) {
          this.key = key;
          this.value = value;
     }

     public String getKey() {
          return key;
     }

     public Object getValue() {
          return value;
     }

     public static CommAttribute create(String key, String value) {
          return new CommAttribute(key, value);
     }

     public static CommAttribute type(String value) {
          return new CommAttribute(CommProperties.TYPE, value);
     }

     public static CommAttribute host(String value) {
          return new CommAttribute(CommProperties.HOST, value);
     }

     public static CommAttribute port(int value) {
          return new CommAttribute(CommProperties.PORT, value);
     }

     public static CommAttribute domain(String value) {
          return new CommAttribute(CommProperties.DOMAIN, value);
     }

     public static CommAttribute weight(int value) {
          return new CommAttribute(CommProperties.WEIGHT, value);
     }

     public static CommAttribute kind() {
          return new CommAttribute(CommProperties.KIND, "comm");
     }

}
