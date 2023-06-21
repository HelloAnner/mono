package com.anner.starter.singleton;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/21
 */
public abstract class MonoShell<T> {
    private T singleton;

    public MonoShell() {
    }

    public final void setSingleton(T singleton) {
        this.singleton = singleton;
    }

    public T get() {
        return singleton;
    }
}
