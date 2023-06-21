package com.anner.comm.common;

import com.anner.comm.info.CommProperties;

public interface CommServer {

     /**
      * 服务权重，这是一个固定值，在对象初始化时被读取，后续更改无效
      */
     int weight();

     /**
      * 服务类型，如http，grpc等
      */
     String type();

     /**
      * 注册服务
      * 
      * @param service 服务处理对象（拥有@CommService注解服务接口的实例）
      */
     void registerService(Object service);

     /**
      * 获取服务的注册信息
      * 
      * @return 注册信息属性列表
      */
     CommProperties getPublishAttributes();
}
