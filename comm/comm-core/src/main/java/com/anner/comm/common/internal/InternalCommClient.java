package com.anner.comm.common.internal;

import static com.anner.comm.info.CommProperties.TYPE;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.anner.comm.common.BaseCommClient;
import com.anner.comm.common.CommClientFactory;
import com.anner.comm.common.internal.helper.ExceptionWrappedInputStream;
import com.anner.comm.info.CommProperties;
import com.anner.comm.loadbalancer.CommLoadBalancer;
import com.anner.comm.stream.CommStreamPipe;

public class InternalCommClient extends BaseCommClient implements CommLoadBalancer {

    private static final String SELF_TYPE = "internal";
    private static final String SERVER = "server";

    private static final int PIPE_BUF_SIZE = 1024 * 1024;

    private final InternalCommServer server;
    private final Map<Class<?>, Object> pMap = new ConcurrentHashMap<>();

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(
            0,
            1000,
            1,
            TimeUnit.MINUTES,
            new SynchronousQueue<>(),
            new InternalClientThreadFactory());

    public InternalCommClient(CommProperties properties) {
        super(properties);

        try {
            server = (InternalCommServer) properties.get(SERVER);
        } catch (Throwable e) {
            throw new RuntimeException("invalid InternalCommServer", e);
        }
        if (server == null) {
            throw new NullPointerException("InternalCommServer");
        }
    }

    // 内置默认实现，这个直接就拿到了server，如果是通信方式，是需要通信获取数据调用的
    public InternalCommClient(InternalCommServer server) {
        this(parseProperties(server));
    }

    static {
        // 类加载时将自身注册到工厂中
        CommClientFactory.registerClient(SELF_TYPE, InternalCommClient::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createCaller(Class<T> callerInterface) {
        T t = (T) server.getService(callerInterface);
        return (T) Proxy.newProxyInstance(
                callerInterface.getClassLoader(),
                new Class[] { callerInterface },
                (proxy, method, args) -> {
                    Object result = method.invoke(t, args);
                    if (result instanceof CommStreamPipe) {
                        // 流返回值类型需要特殊处理
                        return convertPipe((CommStreamPipe) result);
                    }
                    return result;
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCaller(Class<T> callerInterface) {
        return (T) pMap.computeIfAbsent(callerInterface, this::createCaller);
    }

    private static CommProperties parseProperties(InternalCommServer server) {
        CommProperties p = new CommProperties();
        p.put(TYPE, SELF_TYPE);
        p.put(SERVER, server);
        return p;
    }

    private static class InternalClientThreadFactory implements ThreadFactory {

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(null, r, "InternalClient-worker-" + mThreadNum.getAndIncrement());
        }
    }

    private static CommStreamPipe convertPipe(CommStreamPipe pipe) {
        // 流式返回值需要做一个从写模式到读模式的转换
        PipedInputStream in = new PipedInputStream(PIPE_BUF_SIZE);
        PipedOutputStream out;
        try {
            out = new PipedOutputStream(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ExceptionWrappedInputStream ein = new ExceptionWrappedInputStream(in);
        CommStreamPipe result = CommStreamPipe.wrap(ein);
        // 在另一个线程中进行写操作
        EXECUTOR.submit(() -> {
            try {
                pipe.doWrite(out);
            } catch (Throwable e) {
                ein.setException(e);
            } finally {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
        });
        return result;
    }
}
