package com.anner.comm.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.anner.comm.common.BaseCommServer;
import com.anner.comm.grpc.utils.GRPCAdapterRegistry;
import com.anner.comm.info.CommAttribute;
import com.anner.comm.info.CommServiceInfo;
import com.anner.comm.utils.CommAnnotationUtils;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GRPCCommServer extends BaseCommServer {
     private final ServerBuilder<?> serverBuilder;

     private Server server;

     public GRPCCommServer(int port) {
          serverBuilder = ServerBuilder.forPort(port);
          appendAttribute(CommAttribute.port(port));
     }

     public GRPCCommServer(ServerBuilder<?> sb) {
          serverBuilder = sb;
     }

     @Override
     public String type() {
          return "grpc";
     }

     @Override
     @SuppressWarnings("unchecked")
     public synchronized void registerService(Object service) {
          CommServiceInfo info = CommAnnotationUtils.getServicesInfo(service.getClass());
          serverBuilder.addService(GRPCAdapterRegistry.createService((Class<Object>) info.getClazz(), service));
     }

     /**
      * 启动服务
      */
     public synchronized void start() throws IOException {
          if (server == null) {
               server = serverBuilder.build();
          }
          server.start();
          // 在jvm退出时停止
          Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
     }

     /**
      * 停止服务
      */
     public synchronized void stop() {
          if (server != null) {
               try {
                    server.shutdown().awaitTermination(10, TimeUnit.SECONDS);
               } catch (InterruptedException ignore) {
               }
               server = null;
          }
     }

     /**
      * 阻塞当前线程直到服务停止
      */
     public synchronized void blockUntilShutdown() throws InterruptedException {
          if (server != null) {
               server.awaitTermination();
          }
     }

}
