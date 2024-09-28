package com.anner.properties;

import com.anner.common.string.StringUtils;

import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/21
 */
public enum MonoProperties {
    REDIS_ADDR("mono.redis.addr", "");
    private final String key;

    private final Supplier<String> defaultSupplier;

    MonoProperties(String key, String defaultVal) {
        this.key = key;
        this.defaultSupplier = () -> defaultVal;
    }

    MonoProperties(String key, Supplier<String> defaultSupplier) {
        this.key = key;
        this.defaultSupplier = defaultSupplier;
    }

    public String read(Properties properties) {
        String val = properties.getProperty(key);
        return StringUtils.isNotEmpty(val) ? val : defaultSupplier.get();
    }
}
