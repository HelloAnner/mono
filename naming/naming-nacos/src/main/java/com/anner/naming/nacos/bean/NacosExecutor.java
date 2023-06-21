package com.anner.naming.nacos.bean;

import com.alibaba.nacos.api.exception.NacosException;
import com.anner.naming.nacos.exception.NacosWrapperException;

/**
 * Created by anner on 2023/3/23
 */
public interface NacosExecutor<T> {

    T execute() throws NacosException, NacosWrapperException;

    void onError(NacosWrapperException nacosWrapperException);

    default boolean ignoreException(NacosWrapperException nacosWrapperException) {
        return false;
    }
}
