package com.anner.comm.grpc.utils;

/**
 * Created by anner on 2023/3/15
 */
public interface ThrowableTask<E extends Throwable> {

    /**
     * 执行操作
     *
     * @throws E 抛出异常
     */
    void call() throws E;
}
