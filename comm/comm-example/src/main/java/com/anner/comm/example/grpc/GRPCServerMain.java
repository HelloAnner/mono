package com.anner.comm.example.grpc;

import com.anner.comm.CommPublisher;
import com.anner.comm.example.Greeter;
import com.anner.comm.example.GreeterImpl;
import com.anner.comm.example.grpc.adapter.GreeterServiceAdapter;
import com.anner.comm.grpc.GRPCCommServer;
import com.anner.comm.grpc.utils.GRPCAdapterRegistry;

public class GRPCServerMain {
     public static void main(String[] args) throws Exception {
          // 定义grpc通信服务端
          GRPCCommServer grpcServer = new GRPCCommServer(50051);

          // 注册grpc的服务适配器
          // GRPCAdapterRegistry.registerServiceAdapter(Greeter.class,
          // GreeterServiceAdapter::new);

          // 发布服务
          CommPublisher.create()
                    // 通信方式使用grpc
                    .addServer(grpcServer)
                    // 添加服务
                    .addService(new GreeterImpl())
                    // 发布
                    .publish();

          // 启动grpc服务器
          grpcServer.start();
          grpcServer.blockUntilShutdown();
     }
}
