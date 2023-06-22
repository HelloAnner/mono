package com.anner.mq;

import com.anner.common.log.Logger;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/22
 * <p>
 * 全局的消息队列管理
 */
public class MonoMQManagerHub {

    private static MonoMQClient client;


    public synchronized static void initialize(MonoMQClient client) {
        if (client != null && MonoMQManagerHub.client == null) {
            MonoMQManagerHub.client = client;
            Logger.info("mono set mq client is ", client.getClass());
        }
    }

    public static Sub applySubConsumer(String topic) {
        // TODO 这里可以添加多个 listener 机制
        return client.generateSubConsumer(topic);
    }

    public static Pub applyPubProducer(String topic) {
        return client.generatePubProducer(topic);
    }
}
