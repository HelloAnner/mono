package com.anner.comm.http;

import com.anner.comm.annotations.CommService;
import com.anner.comm.stream.CommStreamPipe;
import com.anner.common.reflect.Reflect;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class HTTPCommTest {


    @Test
    public void simple() {
        HTTPCommServer server = new HTTPCommServer();
        server.registerService(new GreeterTestImpl());
        MockHttpClient httpClient = new MockHttpClient(server);
        HTTPCommClient client = new HTTPCommClient("http://localhost:8080/rpc");
        Reflect.on(client).set("httpClient", httpClient);

        GreeterTest greeter = client.createCaller(GreeterTest.class);
        assertEquals("hello tom", greeter.sayHello("tom"));
    }

    @Test
    public void stream() throws Exception {
        HTTPCommServer server = new HTTPCommServer();
        server.registerService(new GreeterTestImpl());
        MockHttpClient httpClient = new MockHttpClient(server);
        HTTPCommClient client = new HTTPCommClient("http://localhost:8080/rpc");
        Reflect.on(client).set("httpClient", httpClient);

        GreeterTest greeter = client.createCaller(GreeterTest.class);
        ByteArrayInputStream bis = new ByteArrayInputStream("Sophia\nJackson\nEmma\n".getBytes(StandardCharsets.UTF_8));
        try (CommStreamPipe streamResult = greeter.sayHelloByStream(CommStreamPipe.wrap(bis))) {
            assertEquals("Hello Sophia\nHello Jackson\nHello Emma\n", IOUtils.toString(streamResult, StandardCharsets.UTF_8));
        }
    }


    @CommService("test.greeter")
    public interface GreeterTest {
        String sayHello(String name);

        CommStreamPipe sayHelloByStream(CommStreamPipe requestStream) throws Exception;
    }

    public static class GreeterTestImpl implements GreeterTest {
        @Override
        public String sayHello(String name) {
            return "hello " + name;
        }

        @Override
        public CommStreamPipe sayHelloByStream(CommStreamPipe requestStream) throws Exception {
            return CommStreamPipe.withWriter(out -> {
                OutputStreamWriter writer = new OutputStreamWriter(out);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 模拟处理时间
                        Thread.sleep(500);
                        writer.write(String.format("Hello %s\n", line));
                        writer.flush();
                    }
                }
            });
        }
    }
}