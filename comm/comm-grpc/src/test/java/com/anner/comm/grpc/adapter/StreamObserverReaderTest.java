package com.anner.comm.grpc.adapter;

import com.google.protobuf.ByteString;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by anner on 2023/3/14
 */
public class StreamObserverReaderTest {

    @Test(timeout = 10000)
    public void getSingle() throws Throwable {
        StreamObserverReader<ByteHolder> reader = new StreamObserverReader<>();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            reader.onNext(new ByteHolder(ByteString.copyFrom("hello world", StandardCharsets.UTF_8)));
            reader.onCompleted();
        }).start();
        reader.waitUntilComplete();

        ByteHolder bh = reader.getSingle();
        assertEquals("hello world", bh.getBs().toString(StandardCharsets.UTF_8));

        bh = reader.getSingle(Exception.class);
        assertEquals("hello world", bh.getBs().toString(StandardCharsets.UTF_8));
    }

    @Test(timeout = 10000, expected = IOException.class)
    public void getSingleWithIOException() throws Throwable {
        StreamObserverReader<ByteHolder> reader = new StreamObserverReader<>();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            reader.onError(new Exception("test IOException"));
            reader.onCompleted();
        }).start();
        reader.waitUntilComplete();

        reader.getSingle(IOException.class);
    }

    @Test(timeout = 10000, expected = Exception.class)
    public void getSingleWithException() throws Throwable {
        StreamObserverReader<ByteHolder> reader = new StreamObserverReader<>();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            reader.onError(new Exception("test IOException"));
            reader.onCompleted();
        }).start();
        reader.waitUntilComplete();

        reader.getSingle();
    }

    @Test(timeout = 10000)
    public void getAll() throws Throwable {
        StreamObserverReader<ByteHolder> reader = new StreamObserverReader<>();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < 3; i++) {
                reader.onNext(new ByteHolder(ByteString.copyFrom("hello world" + i, StandardCharsets.UTF_8)));
            }
            reader.onCompleted();
        }).start();
        reader.waitUntilComplete();

        List<ByteHolder> list = reader.getAll();
        assertEquals(3, list.size());
        assertEquals("hello world0", list.get(0).getBs().toString(StandardCharsets.UTF_8));
        assertEquals("hello world1", list.get(1).getBs().toString(StandardCharsets.UTF_8));
        assertEquals("hello world2", list.get(2).getBs().toString(StandardCharsets.UTF_8));

        list = reader.getAll(Exception.class);
        assertEquals(3, list.size());
    }

    @Test(timeout = 10000, expected = IOException.class)
    public void getAllWithIOException() throws Throwable {
        StreamObserverReader<ByteHolder> reader = new StreamObserverReader<>();
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            reader.onNext(new ByteHolder(ByteString.copyFrom("hello world", StandardCharsets.UTF_8)));
            reader.onError(new Exception("test IOException"));
            reader.onCompleted();
        }).start();
        reader.waitUntilComplete();

        reader.getAll(IOException.class);
    }

    @Test(timeout = 10000, expected = Exception.class)
    public void getAllWithException() throws Throwable {
        StreamObserverReader<ByteHolder> reader = new StreamObserverReader<>();
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            reader.onNext(new ByteHolder(ByteString.copyFrom("hello world", StandardCharsets.UTF_8)));
            reader.onError(new Exception("test IOException"));
            reader.onCompleted();
        }).start();
        reader.waitUntilComplete();

        reader.getAll();
    }
}