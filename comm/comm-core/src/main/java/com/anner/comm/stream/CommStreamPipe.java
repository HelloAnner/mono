package com.anner.comm.stream;


import com.anner.comm.stream.helper.StreamPipeWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>流管道
 * <p>微服务通信中特殊的预定义参数对象，用于流式传输
 * <p>
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <caption>流管道工作逻辑</caption>
 * <tr>
 * <td><b>客户端写（作为参数）</b></td>
 * <td><b>服务端读（作为参数）</b></td>
 * <td><b>服务端写（作为返回值）</b></td>
 * <td><b>客户端读（作为返回值）</b></td>
 * </tr>
 * <tr>
 * <td ALIGN=CENTER>读模式</td>
 * <td ALIGN=CENTER>读模式</td>
 * <td ALIGN=CENTER>写模式</td>
 * <td ALIGN=CENTER>读模式</td>
 * </tr>
 * </table>
 *
 * <ul>
 *     <li>读模式：包装 InputStream 进行读取操作</li>
 *     <li>写模式：传递 OutputStream，只用于服务端写的情况</li>
 * </ul>
 */
public final class CommStreamPipe extends InputStream {

    private InputStream in;
    private StreamPipeWriter writer;

    private CommStreamPipe() {
    }

    /**
     * 传递 OutputStream，并执行写入操作
     *
     * @param out OutputStream
     */
    public void doWrite(OutputStream out) throws Exception {
        if (writer == null) {
            throw new RuntimeException("doWrite should be call in writer mode");
        }
        writer.handle(out);
    }

    @Override
    public int read() throws IOException {
        checkIn();
        return in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        checkIn();
        return in.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        checkIn();
        return in.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        checkIn();
        in.close();
    }

    private void checkIn() {
        if (in == null) {
            throw new RuntimeException("read should be call in reader mode");
        }
    }

    /**
     * 包装 InputStream，读模式
     *
     * @param in 被包装的 InputStream
     */
    public static CommStreamPipe wrap(InputStream in) {
        CommStreamPipe pipe = new CommStreamPipe();
        pipe.in = in;
        return pipe;
    }

    /**
     * 包装流写入方法，写模式
     * <pre>
     * return CommStreamPipe.withWriter(out -> {
     *     //往out中写入数据...
     * });
     * </pre>
     *
     * @param writer 流写入对象
     */
    public static CommStreamPipe withWriter(StreamPipeWriter writer) {
        CommStreamPipe pipe = new CommStreamPipe();
        pipe.writer = writer;
        return pipe;
    }

    @Override
    protected void finalize() throws Throwable {
        if (in != null) {
            in.close();
        }
    }
}
