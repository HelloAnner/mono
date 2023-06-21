package com.anner.comm.example.grpc.adapter;

import com.anner.comm.example.Greeter;
import com.anner.comm.example.grpc.greeter.GreeterGrpc;
import com.anner.comm.example.grpc.greeter.GreeterGrpc.GreeterStub;
import com.anner.comm.example.grpc.greeter.HelloReply;
import com.anner.comm.example.grpc.greeter.HelloReplyBytes;
import com.anner.comm.example.grpc.greeter.HelloRequest;
import com.anner.comm.example.grpc.greeter.HelloRequestBytes;
import com.anner.comm.grpc.adapter.InputStream2ObserverTransmitter;
import com.anner.comm.grpc.adapter.Observer2InputStreamAdapter;
import com.anner.comm.grpc.adapter.StreamObserverReader;
import com.anner.comm.grpc.utils.GRPCAdapterExecutors;
import com.anner.comm.grpc.utils.ThrowableWaiter;
import com.anner.comm.stream.CommStreamPipe;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

/**
 * Greeter的grpc客户端适配器 , 返回一个服务对象
 */
public class GreeterStubAdapter implements Greeter {

    private final GreeterStub stub;

    public GreeterStubAdapter(Channel channel) {
        this.stub = GreeterGrpc.newStub(channel);
        // 异步方式对于非流方式调用适配起来比较麻烦
        // 如果服务中没有流方式调用，也可以用同步方式 GreeterGrpc.newBlockingStub()
    }

    @Override
    public String sayHello(String name) throws Exception {
        StreamObserverReader<HelloReply> reader = new StreamObserverReader<>();
        stub.sayHello(HelloRequest.newBuilder().setName(name).build(), reader);
        reader.waitUntilComplete();
        return reader.getSingle(Exception.class).getMessage();
    }

    @Override
    public String sayHelloToManyPeople(CommStreamPipe requestStream) throws Exception {
        StreamObserverReader<HelloReply> reader = new StreamObserverReader<>();

        InputStream2ObserverTransmitter<HelloRequestBytes> tx = new InputStream2ObserverTransmitter<>(
                bytes -> HelloRequestBytes.newBuilder().setName(bytes).build(),
                requestStream
        );

        ThrowableWaiter<IOException> waiter = GRPCAdapterExecutors.submitThrowableTask(() -> {
            StreamObserver<HelloRequestBytes> req = stub.sayHelloToManyPeople(reader);
            tx.transmit(req);
        });

        waiter.await();
        reader.waitUntilComplete();
        return reader.getSingle(Exception.class).getMessage();
    }

    @Override
    public CommStreamPipe sayHelloMultiTimes(String name) throws Exception {
        Observer2InputStreamAdapter<HelloReplyBytes> resAdapter = new Observer2InputStreamAdapter<>(HelloReplyBytes::getMessage);
        GRPCAdapterExecutors.submit(() -> stub.sayHelloMultiTimes(HelloRequest.newBuilder().setName(name).build(), resAdapter));
        return CommStreamPipe.wrap(resAdapter);
    }

    @Override
    public CommStreamPipe sayHelloByStream(CommStreamPipe requestStream) throws Exception {
        Observer2InputStreamAdapter<HelloReplyBytes> resAdapter = new Observer2InputStreamAdapter<>(HelloReplyBytes::getMessage);
        InputStream2ObserverTransmitter<HelloRequestBytes> tx = new InputStream2ObserverTransmitter<>(
                bytes -> HelloRequestBytes.newBuilder().setName(bytes).build(),
                requestStream
        );
        GRPCAdapterExecutors.submit(() -> {
            StreamObserver<HelloRequestBytes> req = stub.sayHelloByStream(resAdapter);
            try {
                tx.transmit(req);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return CommStreamPipe.wrap(resAdapter);
    }

}
