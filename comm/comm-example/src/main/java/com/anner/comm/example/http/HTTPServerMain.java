package com.anner.comm.example.http;

import com.anner.comm.CommPublisher;
import com.anner.comm.example.GreeterImpl;
import com.anner.comm.http.HTTPCommServer;
import com.anner.comm.info.CommAttribute;
import com.anner.comm.info.CommPublishInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by anner on 2023/3/21
 */
@SpringBootApplication
@RestController
public class HTTPServerMain {

    // 定义http通信服务端
    private static final HTTPCommServer HTTP_SERVER = new HTTPCommServer();

    public static void main(String[] args) {
        // 发布服务
        CommPublishInfo publishInfo = CommPublisher.create()
                // 通信方式使用http，http方式需要从外部额外添加服务属性（端口、路径）
                .addServer(HTTP_SERVER, CommAttribute.port(8080), CommAttribute.create("path", "/rpc"))
                // 添加服务
                .addService(new GreeterImpl())
                // 发布
                .publish();
        // TODO 将publishInfo提交到服务注册发现组件，此处忽略

        // 启动http服务器
        SpringApplication.run(HTTPServerMain.class, args);
    }

    @PostMapping("/rpc")
    public static void handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        HTTP_SERVER.handleRequest(req, res);
    }

}
