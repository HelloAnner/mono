package com.anner.mq;

import java.util.function.Consumer;

/**
 * @author anner
 * @version 11.0
 * Created on 2023/6/22
 */
public interface MQConsumer {
    void startListen(String topic, Consumer<String> callback);
}
