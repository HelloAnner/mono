package com.anner.comm.stream.helper;

import java.io.OutputStream;

/**
 * 流管道写入接口
 */
public interface StreamPipeWriter {

    /**
     * 处理流写入操作
     *
     * @param out 接收数据的流
     */
    void handle(OutputStream out) throws Exception;
}
