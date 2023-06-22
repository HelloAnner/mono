package com.anner.mq.redis;

import com.anner.mq.MQConsumer;
import com.anner.mq.Sub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.function.Consumer;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/22
 */
public class RedisSubConsumer implements MQConsumer, Sub {
    private Jedis jedis;

    public RedisSubConsumer(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void startListen(String topic, Consumer<String> callback) {
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                callback.accept(message);
            }
        }, topic);
    }
}
