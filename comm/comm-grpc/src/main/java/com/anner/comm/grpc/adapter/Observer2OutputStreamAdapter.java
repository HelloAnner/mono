package com.anner.comm.grpc.adapter;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;

/**
 * 将StreamObserver包装为OutputStream的适配器 - 使用组合的形式
 * 数据流向 --> OutputStream(write bytes) --> StreamObserver(write data)
 */
public class Observer2OutputStreamAdapter<V> extends OutputStream {

    private final Function<ByteString, V> fromBytesMapper;

    // 内置一个流行为的监视器
    private final StreamObserver<V> observer;
    private final byte[] buf = new byte[GRPCAdapterConstants.BUF_SIZE];
    private int count;

    public Observer2OutputStreamAdapter(Function<ByteString, V> fromBytesMapper, StreamObserver<V> observer) {
        this.fromBytesMapper = fromBytesMapper;
        this.observer = observer;
    }


    @Override
    public void write(int b) throws IOException {
        buf[count++] = (byte) b;
        if (count == buf.length) {
            flush();
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // https://www.notion.so/NIO-Buffer-ed65bfe4c6924e82a17f1627cd23ffd1?pvs=4
        while (len > 0) {
            if (len + count >= buf.length) {
                int countThisTime = buf.length - count;
                System.arraycopy(b, off, buf, count, countThisTime);
                len -= countThisTime;
                off += countThisTime;
                count += countThisTime;
                flush();
            } else {
                System.arraycopy(b, off, buf, count, len);
                count += len;
                len = 0;
            }
        }
    }

    @Override
    public void flush() throws IOException {
        if (count > 0) {
            observer.onNext(fromBytesMapper.apply(ByteString.copyFrom(buf, 0, count)));
            count = 0;
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        observer.onCompleted();
    }
}
