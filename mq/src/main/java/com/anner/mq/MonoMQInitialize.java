package com.anner.mq;

import com.anner.mq.redis.RedisMQClient;
import redis.clients.jedis.Jedis;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/22
 * <p>
 * MQ 客户端的初始化
 */
public class MonoMQInitialize {

    public static RedisMQClient applyRedisClient(Jedis jedis) {
        // 每一次都是一个新的 client
        return new RedisMQClient(jedis);
    }
}
