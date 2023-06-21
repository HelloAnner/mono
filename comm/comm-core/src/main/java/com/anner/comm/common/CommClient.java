package com.anner.comm.common;

/**
 * 微服务客户端
 */
public interface CommClient {
  /**
   * 返回客户端实例的唯一标识，一般使用ip:port等组合来生成
   */
  String identifier();

  /**
   * 创建服务调用对象
   * 
   * @param callerInterface 服务调用接口（拥有@CommService注解的服务接口）
   * @return 被代理的接口实例
   */
  <T> T createCaller(Class<T> callerInterface);

  /**
   * 服务权重（该权重为固定值，在客户端生命周期内不变）
   */
  int weight();

  /**
   * 设置自定义属性，一般用于记录负载均衡相关对象
   * 
   * @param key   属性key
   * @param value 属性值
   */
  void putAttribute(String key, Object value);

  /**
   * 获取自定义属性
   * 
   * @param key 属性key
   * @return 属性值
   */
  <T> T getAttribute(String key);
}
