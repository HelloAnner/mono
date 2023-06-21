package com.anner.comm.grpc.adapter;

import com.google.protobuf.ByteString;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;


/**
 * Created by anner on 2023/3/14
 */
public class Observer2InputStreamAdapterTest {

    @Test(timeout = 1000)
    public void transport() throws IOException {
        Observer2InputStreamAdapter<ByteHolder> adapter = new Observer2InputStreamAdapter<>(ByteHolder::getBs);

        new Thread(() -> {
            adapter.onNext(new ByteHolder(ByteString.copyFrom("hello world", StandardCharsets.UTF_8)));
            adapter.onCompleted();
        }).start();

        try (InputStream in = adapter) {
            byte[] buf = new byte[11];
            assertEquals(5, in.read(buf, 0, 5));
            buf[5] = (byte) in.read();
            byte[] buf2 = new byte[5];
            assertEquals(5, in.read(buf2));
            System.arraycopy(buf2, 0, buf, 6, buf2.length);
            assertEquals("hello world", new String(buf));
        }
    }

    @Test(expected = IOException.class)
    public void transportWithException() throws IOException {
        Observer2InputStreamAdapter<ByteHolder> adapter = new Observer2InputStreamAdapter<>(ByteHolder::getBs);

        new Thread(() -> {
            adapter.onError(new IOException("test IOException"));
            adapter.onCompleted();
        }).start();

        try (InputStream in = adapter) {
            byte[] buf = new byte[11];
            //noinspection ResultOfMethodCallIgnored
            in.read(buf);
        }
    }
}
