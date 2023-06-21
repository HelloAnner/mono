package com.anner.naming.nacos.exception;

import com.alibaba.nacos.api.exception.NacosException;

/**
 * Created by anner on 2023/3/23
 */
public class NacosExceptionHelper {

    /**
     * 异常转换
     * 注意NacosException最终目的是对外暴露errorCode、errorMsg, 所以这里不必去获取throwable
     */
    public final static NacosWrapperException toNacosWrapperException(NacosException nacosException) {
        return new NacosWrapperException(nacosException.getErrCode(), nacosException.getErrMsg());
    }
}
