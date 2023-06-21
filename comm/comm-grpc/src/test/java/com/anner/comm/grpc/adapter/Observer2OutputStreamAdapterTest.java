package com.anner.comm.grpc.adapter;

import io.grpc.stub.StreamObserver;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * Created by anner on 2023/3/14
 */
public class Observer2OutputStreamAdapterTest {

    @Test
    public void transmit() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        StreamObserver<ByteHolder> so = new StreamObserver<ByteHolder>() {
            @Override
            public void onNext(ByteHolder byteHolder) {
                try {
                    out.write(byteHolder.getBs().toByteArray());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throw new RuntimeException(throwable);
            }

            @Override
            public void onCompleted() {
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Observer2OutputStreamAdapter<ByteHolder> adapter = new Observer2OutputStreamAdapter<>(ByteHolder::new, so);
        for (int i = 0; i < 300; i++) {
            adapter.write("helloworld".getBytes(StandardCharsets.UTF_8));
        }
        adapter.close();

        byte[] result = out.toByteArray();
        assertEquals(3000, result.length);
        assertEquals("helloworld", new String(result, 0, 10));
    }
}