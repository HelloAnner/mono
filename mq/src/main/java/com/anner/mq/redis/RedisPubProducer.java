package com.anner.mq.redis;

import com.anner.mq.MQProducer;
import com.anner.mq.Pub;
import redis.clients.jedis.Jedis;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/22
 */
public class RedisPubProducer implements Pub, MQProducer {
    private Jedis jedis;

    public RedisPubProducer(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void publish(String topic, byte[] message) {
        jedis.publish(topic, new String(message));
    }
}
