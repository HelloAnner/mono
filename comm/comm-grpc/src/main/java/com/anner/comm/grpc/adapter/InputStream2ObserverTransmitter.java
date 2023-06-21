package com.anner.comm.grpc.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

/**
 * 封装一下， 将InputStream中的数据转化为StreamObserver写入的传输器
 * 数据流向 --> InputStream(read bytes) --> StreamObserver(accepts data) ->
 */
public class InputStream2ObserverTransmitter<V> {
     private final Function<ByteString, V> fromBytesMapper;

     private final InputStream in;

     public InputStream2ObserverTransmitter(Function<ByteString, V> fromBytesMapper, InputStream in) {
          this.fromBytesMapper = fromBytesMapper;
          this.in = in;
     }

     /**
      * 传输数据，将 inputStream 转换为 streamObserver
      * 
      * @param ob 监视流的操作
      * @throws IOException 异常
      */
     public void transmit(StreamObserver<V> ob) throws IOException {
          byte[] buf = new byte[GRPCAdapterConstants.BUF_SIZE];
          int count;

          try {
               while ((count = in.read(buf)) > 0) {
                    ob.onNext(fromBytesMapper.apply(ByteString.copyFrom(buf, 0, count)));
               }
          } catch (IOException ioException) {
               ob.onError(ioException);
          } catch (Throwable t) {
               ob.onError(t);
               throw new IOException(t);
          } finally {
               ob.onCompleted();
               in.close();
          }
     }

}
