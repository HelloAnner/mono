package com.anner.comm.info;

/**
 * 服务注册信息，代表一个服务端对象及其支持的服务列表;
 * 如一个grpc服务支持Greeter、Exporter等服务
 */
public class CommServerInfo {
     private final CommProperties serverProperties;

     private final CommServiceInfo[] services;

     public CommServerInfo(CommProperties serverProperties, CommServiceInfo[] services) {
          this.serverProperties = serverProperties;
          this.services = services;
     }

     public CommProperties getServerProperties() {
          return serverProperties;
     }

     public CommServiceInfo[] getServices() {
          return services;
     }
}
