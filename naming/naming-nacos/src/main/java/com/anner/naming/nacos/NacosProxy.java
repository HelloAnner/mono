package com.anner.naming.nacos;


import com.alibaba.nacos.api.exception.NacosException;
import com.anner.naming.nacos.bean.NacosExecutor;
import com.anner.naming.nacos.exception.NacosExceptionHelper;
import com.anner.naming.nacos.exception.NacosWrapperException;

/**
 * Created by anner on 2023/3/23
 */
public class NacosProxy {

    public final static <T> T apply(NacosExecutor<T> executor) throws NacosWrapperException {

        try {
            return executor.execute();
        } catch (NacosException | NacosWrapperException e) {
            NacosWrapperException wrapperException;
            if (e instanceof NacosException) {
                wrapperException = NacosExceptionHelper.toNacosWrapperException((NacosException) e);
            } else {
                wrapperException = (NacosWrapperException) e;
            }
            executor.onError(wrapperException);
            if (executor.ignoreException(wrapperException)) {
                return null;
            }
            throw wrapperException;
        }
    }
}
