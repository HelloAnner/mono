package com.anner.mq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/22
 * <p>
 * 消息队列的实现的client
 * <p>
 * 1. 内存实现
 * 2. redis 实现
 * 3. 第三方mq实现 ，如 rocketMQ
 */
public abstract class MonoMQClient {

    private final ConcurrentHashMap<String, List<Sub>> allSubConsumers = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, List<Pub>> allPubProducers = new ConcurrentHashMap<>();

    protected abstract Sub newSubConsumer(String topic);

    protected abstract Pub newPubProducer(String topic);

    public Sub generateSubConsumer(String topic) {
        Sub sub = newSubConsumer(topic);
        allSubConsumers.computeIfAbsent(topic, k -> new ArrayList<>()).add(sub);
        return sub;
    }

    public Pub generatePubProducer(String topic) {
        Pub pub = newPubProducer(topic);
        allPubProducers.computeIfAbsent(topic, k -> new ArrayList<>()).add(pub);
        return pub;
    }
}
