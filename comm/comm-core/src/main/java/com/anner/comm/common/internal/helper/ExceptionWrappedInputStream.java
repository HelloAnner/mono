package com.anner.comm.common.internal.helper;


import java.io.IOException;
import java.io.InputStream;

/**
 * Created by loy on 2022/10/10.
 *
 * <p>支持抛出额外异常的InputStream
 */
public class ExceptionWrappedInputStream extends InputStream {

    private final InputStream in;
    private volatile IOException exception;

    public ExceptionWrappedInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        checkExp();
        try {
            return in.read();
        } catch (IOException e) {
            checkExp();
            throw e;
        } finally {
            checkExp();
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        checkExp();
        try {
            return in.read(b, off, len);
        } catch (IOException e) {
            checkExp();
            throw e;
        } finally {
            checkExp();
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    public synchronized void setException(Throwable e) {
        exception = e instanceof IOException ? (IOException) e : new IOException(e);
    }

    private synchronized void checkExp() throws IOException {
        if (exception != null) {
            throw exception;
        }
    }
}
