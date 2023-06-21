package com.anner.comm.http.helper;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * http传输对象封装类
 * <p>
 * Created by anner on 2023/3/21
 */
public class HTTPArgsPayload {

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    private Object[] args;

    public HTTPArgsPayload() {
    }

    public HTTPArgsPayload(Object[] args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
