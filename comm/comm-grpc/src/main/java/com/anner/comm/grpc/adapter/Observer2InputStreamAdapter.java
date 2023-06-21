package com.anner.comm.grpc.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.function.Function;

import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

/**
 * 将StreamObserver包装为InputStream的适配器
 * 数据流向为 --> StreamObserver(accepts data) --> InputStream(read bytes) -->
 */
public class Observer2InputStreamAdapter<V> extends InputStream implements StreamObserver<V> {

     private Function<V, ByteString> toBytesMapper;

     private PipedInputStream pin;

     private PipedOutputStream pout;

     private Throwable throwable;

     public Observer2InputStreamAdapter(Function<V, ByteString> toBytesMapper) {
          this.toBytesMapper = toBytesMapper;

          pin = new PipedInputStream(GRPCAdapterConstants.PIPE_BUF_SIZE);

          try {
               pout = new PipedOutputStream(pin);
          } catch (IOException e) {
               throw new RuntimeException(e);
          }
     }

     @Override
     public int read() throws IOException {
          checkThrowable();
          int r = pin.read();
          checkThrowable();
          return r;
     }

     @Override
     public int read(byte[] b, int off, int len) throws IOException {
          checkThrowable();
          int r = pin.read(b, off, len);
          checkThrowable();
          return r;
     }

     @Override
     public void close() throws IOException {
          pin.close();
     }

     @Override
     public void onNext(V value) {
          // 处理输入的数据
          try {
               pout.write(toBytesMapper.apply(value).toByteArray());
               pout.flush();
          } catch (IOException e) {
               consumeException(e);
          }
     }

     @Override
     public void onError(Throwable t) {
          throwable = t;
     }

     @Override
     public void onCompleted() {
          try {
               pout.close();
          } catch (IOException e) {
               consumeException(e);
          }
     }

     private void checkThrowable() throws IOException {
          if (throwable != null) {
               throw new IOException(throwable);
          }
     }

     private void consumeException(Throwable e) {
          e.printStackTrace();
     }

}
