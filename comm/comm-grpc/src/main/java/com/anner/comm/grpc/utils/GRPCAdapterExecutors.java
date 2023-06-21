package com.anner.comm.grpc.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于grpc适配器的线程池
 * <p>
 * Created by anner on 2023/3/15
 */
public class GRPCAdapterExecutors {
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(
            0,
            1000,
            1,
            TimeUnit.MINUTES,
            new SynchronousQueue<>(),
            new GRPCThreadFactory()
    );

    private GRPCAdapterExecutors() {
    }

    public static void submit(Runnable runnable) {
        EXECUTOR.submit(runnable);
    }

    public static <E extends Throwable> ThrowableWaiter<E> submitThrowableTask(ThrowableTask<E> task) {
        Throwable[] thrRef = new Throwable[1];
        CountDownLatch done = new CountDownLatch(1);
        EXECUTOR.submit(() -> {
            try {
                task.call();
            } catch (Throwable t) {
                thrRef[0] = t;
            }
            done.countDown();
        });
        return () -> {
            done.await();
            if (thrRef[0] != null) {
                throw (E) thrRef[0];
            }
        };
    }


    private static class GRPCThreadFactory implements ThreadFactory {

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(null, r, "GRPCAdapterExecutors-worker-" + mThreadNum.getAndIncrement());
        }
    }
}
