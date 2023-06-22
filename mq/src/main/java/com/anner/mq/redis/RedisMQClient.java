package com.anner.mq.redis;

import com.anner.mq.MonoMQClient;
import com.anner.mq.Pub;
import com.anner.mq.Sub;
import redis.clients.jedis.Jedis;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/22
 */
public class RedisMQClient extends MonoMQClient {

    private Jedis jedis;

    public RedisMQClient(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    protected Sub newSubConsumer(String topic) {
        RedisSubConsumer consumer = new RedisSubConsumer(jedis);
        return consumer;
    }

    @Override
    protected Pub newPubProducer(String topic) {
        RedisPubProducer producer = new RedisPubProducer(jedis);
        return producer;
    }
}
