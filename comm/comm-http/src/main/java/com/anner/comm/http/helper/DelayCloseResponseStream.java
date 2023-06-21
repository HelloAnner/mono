package com.anner.comm.http.helper;

import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * 延迟关闭的响应流，在流关闭时触发http响应的关闭
 * <p>
 * Created by anner on 2023/3/21
 */
public class DelayCloseResponseStream extends InputStream {
    private final InputStream in;
    private final CloseableHttpResponse response;

    public DelayCloseResponseStream(InputStream in, CloseableHttpResponse response) {
        this.in = in;
        this.response = response;
    }


    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        try {
            in.close();
        } finally {
            response.close();
        }
    }
}
