package com.anner.redis;

import redis.clients.jedis.Jedis;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/22
 */
public class RedisClient {

    private Jedis jedis;

    public RedisClient(Jedis jedis) {
        this.jedis = jedis;
    }
}
