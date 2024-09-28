package com.anner.common.log;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * Created by anner on 2023/3/15
 */
public class Logger {

    private final static Log inner = LogFactory.get("MONO");

    public static void info(String format, Object... arguments) {
        inner.info(format, arguments);
    }

    public static void info(Throwable t, String format, Object... arguments) {
        inner.info(t, format, arguments);
    }

    public static void info(Throwable t) {
        inner.info(t);
    }

    public static void error(String format, Object... arguments) {
        inner.error(format, arguments);
    }

    public static void error(Throwable t, String format, Object... arguments) {
        inner.error(t, format, arguments);
    }

    public static void error(Throwable t) {
        inner.error(t);
    }

    public static void warn(String format, Object... arguments) {
        inner.warn(format, arguments);
    }

    public static void warn(Throwable t, String format, Object... arguments) {
        inner.warn(t, format, arguments);
    }

    public static void warn(Throwable t) {
        inner.warn(t);
    }
}
