package com.anner.starter;

import com.anner.common.log.Logger;
import com.anner.starter.singleton.MonoShell;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/21
 */
public class MonoContext {

    private final static ConcurrentHashMap<Class<?>, MonoShell<?>> MONO_SIGNAL_SHELL = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> MonoShell<T> singleton(Class<? extends MonoShell<T>> clazz) {
        return (MonoShell<T>) MONO_SIGNAL_SHELL.computeIfAbsent(clazz, k -> newInstance(clazz));
    }


    private static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }

        return null;
    }
}
