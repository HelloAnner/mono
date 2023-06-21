package com.anner.comm.example;

import com.anner.comm.annotations.CommService;
import com.anner.comm.stream.CommStreamPipe;

/**
 * <p>
 * 测试服务接口
 */
@CommService("example.greeter")
public interface Greeter {

    /**
     * 服务提供方法
     *
     * @param name 名称
     * @return 返回 Hello+名称
     */
    String sayHello(String name) throws Exception;

    /**
     * 流式请求
     *
     * @param requestStream 输入流
     * @return 返回 Hello+名称
     */
    String sayHelloToManyPeople(CommStreamPipe requestStream) throws Exception;

    /**
     * 流式响应
     *
     * @param name 名称
     * @return 输出流
     */
    CommStreamPipe sayHelloMultiTimes(String name) throws Exception;

    /**
     * 双向流式
     *
     * @param requestStream 输入流
     * @return 输出流
     */
    CommStreamPipe sayHelloByStream(CommStreamPipe requestStream) throws Exception;
}
