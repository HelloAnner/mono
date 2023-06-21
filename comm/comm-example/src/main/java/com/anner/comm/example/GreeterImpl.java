package com.anner.comm.example;


import com.anner.comm.stream.CommStreamPipe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 测试服务实现
 */
public class GreeterImpl implements Greeter {
    @Override
    public String sayHello(String name) throws Exception {
        return "Hello " + name;
    }

    @Override
    public String sayHelloToManyPeople(CommStreamPipe requestStream) throws Exception {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 模拟处理时间
                Thread.sleep(500);
                builder.append(String.format("Hello %s\n", line));
            }
        }
        return builder.toString();
    }

    @Override
    public CommStreamPipe sayHelloMultiTimes(String name) throws Exception {
        return CommStreamPipe.withWriter(out -> {
            OutputStreamWriter writer = new OutputStreamWriter(out);
            for (int i = 0; i < 3; i++) {
                // 模拟处理时间
                Thread.sleep(500);
                writer.write(String.format("Hello %s\n", name));
                writer.flush();
            }
        });
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
