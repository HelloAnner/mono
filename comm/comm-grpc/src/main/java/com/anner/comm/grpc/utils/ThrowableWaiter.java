package com.anner.comm.grpc.utils;

/**
 * Created by anner on 2023/3/15
 */
public interface ThrowableWaiter<E extends Throwable> {

    /**
     * 异步等待
     *
     * @throws E 异常
     */
    void await() throws E, InterruptedException;
}
