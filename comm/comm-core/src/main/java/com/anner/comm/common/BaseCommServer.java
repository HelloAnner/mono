package com.anner.comm.common;

import com.anner.comm.info.CommAttribute;
import com.anner.comm.info.CommProperties;

public abstract class BaseCommServer implements CommServer {
     private final CommProperties properties = new CommProperties();

     public BaseCommServer() {
          appendAttribute(CommAttribute.type(type()));
          appendAttribute(CommAttribute.weight(weight()));
     }

     @Override
     public int weight() {
          // 默认权重为1，子类可以根据自身配置进行覆盖
          return 1;
     }

     @Override
     public CommProperties getPublishAttributes() {
          try {
               return properties.clone();
          } catch (CloneNotSupportedException e) {
               throw new UnsupportedOperationException(e);
          }
     }

     /**
      * 在子类中添加额外的属性
      */
     protected void appendAttribute(CommAttribute attr) {
          properties.put(attr);
     }

}
