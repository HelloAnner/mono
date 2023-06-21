package com.anner.naming.nacos.discovery.api;

import com.alibaba.nacos.api.naming.NamingService;
import com.anner.naming.nacos.discovery.impl.NacosDiscoveryService;

/**
 * Created by anner on 2023/3/23
 */
public class DiscoveryServiceFactory {

    public static DiscoveryService createDiscoveryService(NamingService namingService) {
        return new NacosDiscoveryService(namingService);
    }
}
