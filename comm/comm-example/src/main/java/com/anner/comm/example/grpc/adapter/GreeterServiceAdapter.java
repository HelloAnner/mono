package com.anner.comm.example.grpc.adapter;

import com.anner.comm.example.Greeter;
import com.anner.comm.example.grpc.greeter.GreeterGrpc.GreeterImplBase;
import com.anner.comm.example.grpc.greeter.HelloReply;
import com.anner.comm.example.grpc.greeter.HelloReplyBytes;
import com.anner.comm.example.grpc.greeter.HelloRequest;
import com.anner.comm.example.grpc.greeter.HelloRequestBytes;
import com.anner.comm.grpc.adapter.Observer2InputStreamAdapter;
import com.anner.comm.grpc.adapter.Observer2OutputStreamAdapter;
import com.anner.comm.grpc.utils.GRPCAdapterExecutors;
import com.anner.comm.stream.CommStreamPipe;
import io.grpc.stub.StreamObserver;

/**
 * Greeter的grpc服务端适配器
 */
public class GreeterServiceAdapter extends GreeterImplBase {

    private final Greeter greeter;

    public GreeterServiceAdapter(Greeter greeter) {
        this.greeter = greeter;
    }

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        try {
            String result = greeter.sayHello(request.getName());
            responseObserver.onNext(HelloReply.newBuilder().setMessage(result).build());
        } catch (Throwable e) {
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<HelloRequestBytes> sayHelloToManyPeople(StreamObserver<HelloReply> responseObserver) {
        Observer2InputStreamAdapter<HelloRequestBytes> reqAdapter = new Observer2InputStreamAdapter<>(HelloRequestBytes::getName);
        GRPCAdapterExecutors.submit(() -> {
            try {
                String res = greeter.sayHelloToManyPeople(CommStreamPipe.wrap(reqAdapter));
                responseObserver.onNext(HelloReply.newBuilder().setMessage(res).build());
            } catch (Throwable t) {
                responseObserver.onError(t);
            } finally {
                responseObserver.onCompleted();
            }
        });
        return reqAdapter;
    }

    @Override
    public void sayHelloMultiTimes(HelloRequest request, StreamObserver<HelloReplyBytes> responseObserver) {
        GRPCAdapterExecutors.submit(() -> {
            try {
                CommStreamPipe result = greeter.sayHelloMultiTimes(request.getName());
                try (Observer2OutputStreamAdapter<HelloReplyBytes> adapter = new Observer2OutputStreamAdapter<>(
                        bytes -> HelloReplyBytes.newBuilder().setMessage(bytes).build(),
                        responseObserver
                )) {
                    result.doWrite(adapter);
                }
            } catch (Throwable e) {
                responseObserver.onError(e);
            } finally {
                responseObserver.onCompleted();
            }
        });
    }

    @Override
    public StreamObserver<HelloRequestBytes> sayHelloByStream(StreamObserver<HelloReplyBytes> responseObserver) {
        Observer2InputStreamAdapter<HelloRequestBytes> reqAdapter = new Observer2InputStreamAdapter<>(HelloRequestBytes::getName);
        GRPCAdapterExecutors.submit(() -> {
            try {
                CommStreamPipe result = greeter.sayHelloByStream(CommStreamPipe.wrap(reqAdapter));
                try (Observer2OutputStreamAdapter<HelloReplyBytes> adapter = new Observer2OutputStreamAdapter<>(
                        bytes -> HelloReplyBytes.newBuilder().setMessage(bytes).build(),
                        responseObserver
                )) {
                    result.doWrite(adapter);
                }
            } catch (Throwable e) {
                responseObserver.onError(e);
            } finally {
                responseObserver.onCompleted();
            }
        });
        return reqAdapter;
    }
}
