package com.anner.mq;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/22
 * <p>
 * 不同场景下都是存在 producer 的
 * <p>
 * 比如发布订阅场景 、点对点的消息队列场景
 */
public interface MQProducer {
    void publish(String topic, byte[] message);
}
