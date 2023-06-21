package com.anner.comm.example.http;

import com.anner.comm.CommRepo;
import com.anner.comm.example.Greeter;
import com.anner.comm.http.HTTPCommClient;
import com.anner.comm.loadbalancer.DefaultLoadBalancer;
import com.anner.comm.stream.CommStreamPipe;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by anner on 2023/3/21
 */
public class HTTPClientMain {

    public static void main(String[] args) throws Exception {
        // 创建默认的负载均衡器，添加一个http节点
        DefaultLoadBalancer loadBalancer = new DefaultLoadBalancer();
        loadBalancer.addClient(new HTTPCommClient("http://localhost:8080/rpc"));

        // 注册负载均衡器
        CommRepo.registerLoadBalancer("example", loadBalancer);

        // 调用方法
        String result = CommRepo.getCaller("example", Greeter.class).sayHello("Jackson");
        System.out.println("[调用方法] result:\n" + result);
        System.out.println();

        // 流式请求
        ByteArrayInputStream bis = new ByteArrayInputStream("Sophia\nJackson\nEmma\n".getBytes(StandardCharsets.UTF_8));
        result = CommRepo.getCaller("example", Greeter.class).sayHelloToManyPeople(CommStreamPipe.wrap(bis));
        System.out.println("[流式请求] result:\n" + result);
        System.out.println();

        // 流式响应
        try (CommStreamPipe streamResult = CommRepo.getCaller("example", Greeter.class)
                .sayHelloMultiTimes("Jackson")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(streamResult));
            System.out.println("[流式响应] result:");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        System.out.println();

        // 双向流式
        bis = new ByteArrayInputStream("Sophia\nJackson\nEmma\n".getBytes(StandardCharsets.UTF_8));
        try (CommStreamPipe streamResult = CommRepo.getCaller("example", Greeter.class)
                .sayHelloByStream(CommStreamPipe.wrap(bis))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(streamResult));
            System.out.println("[双向流式] result:");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        System.out.println();
    }
}
