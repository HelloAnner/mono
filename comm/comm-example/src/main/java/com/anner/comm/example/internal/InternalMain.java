package com.anner.comm.example.internal;

import com.anner.comm.CommPublisher;
import com.anner.comm.CommRepo;
import com.anner.comm.common.internal.InternalCommClient;
import com.anner.comm.common.internal.InternalCommServer;
import com.anner.comm.example.Greeter;
import com.anner.comm.example.GreeterImpl;

/**
 * @author anner
 * @date 2023/3/9
 */
public class InternalMain {
    public static void main(String[] args) throws Exception {
        // 定义内置服务端和客户端
        InternalCommServer server = new InternalCommServer();
        InternalCommClient client = new InternalCommClient(server);

        // 注册服务
        CommPublisher.create()
                .addServer(server)
                .addService(new GreeterImpl())
                .publish();

        // 注册负载均衡器（InternalCommClient实现了负载均衡器接口）
        CommRepo.registerLoadBalancer("example", client);

        // 调用方法
        String result = CommRepo.getCaller("example", Greeter.class).sayHello("Jackson");
        System.out.println("[调用方法] result:\n" + result);
        System.out.println();

    }
}
