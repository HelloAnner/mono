package com.anner.redis;


import com.anner.common.log.Logger;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/21
 * <p>
 * redis 连接对象获取
 */
public class RedisManager {
    private static String addr;
    private static int port;
    private static String auth;

    private static final int MAX_TOTAL = 20;
    private static final int MAX_IDLE = 10;
    private static final int MIN_IDLE = 5;
    private static final long MAX_WAIT_MILLIS = 10000;

    private static JedisPool jedisPool = null;

    public static synchronized void init(String addr, int port, String auth) {
        if (jedisPool != null) {
            jedisPool.close();
            Logger.warn("redis pool is closed , prepare to reload redis pool config");
        }

        RedisManager.addr = addr;
        RedisManager.port = port;
        RedisManager.auth = auth;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(MAX_TOTAL);
        jedisPoolConfig.setMaxIdle(MAX_IDLE);
        jedisPoolConfig.setMinIdle(MIN_IDLE);
        jedisPoolConfig.setMaxWait(Duration.ofMillis(MAX_WAIT_MILLIS));
        jedisPoolConfig.setTestOnBorrow(false);

        RedisManager.jedisPool = new JedisPool(jedisPoolConfig, addr, port);
    }


    public static RedisClient apply() {
        // 申请一个 client 对象
        try {
            return new RedisClient(jedisPool.getResource());
        } catch (Exception e) {
            Logger.error(e, "apply jedis fail:", e.getMessage());
        }
        return null;
    }
}
