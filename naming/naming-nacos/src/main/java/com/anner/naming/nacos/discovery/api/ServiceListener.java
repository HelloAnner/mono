package com.anner.naming.nacos.discovery.api;

import com.anner.naming.nacos.bean.NacosInstance;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by anner on 2023/3/22
 */
public interface ServiceListener {

    /**
     * 监听器的id，需要保证唯一性，建议使用UUID
     */
    String listenerId();

    /**
     * 如果 onEvent()中有耗时操作，建议提供一个Executor来异步处理
     */
    default Executor getExecutor() {
        return null;
    }

    /**
     * 注册的服务有状态变动时，由服务注册发现中心推送变动后的数据
     *
     * @param fineInstanceList 变动后的实例数据
     */
    void onEvent(List<NacosInstance> fineInstanceList);

}
