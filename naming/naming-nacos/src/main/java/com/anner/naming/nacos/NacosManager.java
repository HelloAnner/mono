package com.anner.naming.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.anner.common.log.Logger;
import com.anner.naming.nacos.constant.NacosConstants;
import com.anner.naming.nacos.discovery.api.DiscoveryService;
import com.anner.naming.nacos.discovery.api.DiscoveryServiceFactory;
import com.anner.naming.nacos.exception.NacosExceptionHelper;
import com.anner.naming.nacos.exception.NacosWrapperException;
import com.anner.naming.nacos.register.api.RegisterService;
import com.anner.naming.nacos.register.api.RegisterServiceFactory;

import java.util.Properties;

import static com.anner.naming.nacos.constant.NacosConstants.Naming.NAMESPACE_ID;
import static com.anner.naming.nacos.constant.NacosConstants.Server.PASSWORD;
import static com.anner.naming.nacos.constant.NacosConstants.Server.USER;

/**
 * Nacos 服务发现管理
 * <p>
 * Created by anner on 2023/3/23
 */
public class NacosManager {

    private static NamingService NAMING_SERVICE;
    private static NamingMaintainService NAMING_MAINTAIN_SERVICE;

    private static void init() throws NacosWrapperException {
        Properties properties = new Properties();
        // 服务器地址
        properties.put(PropertyKeyConst.SERVER_ADDR, NacosConstants.Server.IP + ":" + NacosConstants.Server.PORT);
        // 命名空间
        properties.put(PropertyKeyConst.NAMESPACE, NAMESPACE_ID);
        properties.put(PropertyKeyConst.USERNAME, USER);
        properties.put(PropertyKeyConst.PASSWORD, PASSWORD);
        try {
            // 向服务器申请
            NAMING_SERVICE = NacosFactory.createNamingService(properties);
            NAMING_MAINTAIN_SERVICE = NacosFactory.createMaintainService(properties);
        } catch (NacosException e) {
            Logger.error("Init SRDManager failed: create naming service error.", e);
            throw NacosExceptionHelper.toNacosWrapperException(e);
        }
    }

    public static NamingService getNamingService() throws NacosWrapperException {
        if (NAMING_SERVICE == null) init();
        return NAMING_SERVICE;
    }

    public static NamingMaintainService getNamingMaintainService() throws NacosWrapperException {
        if (NAMING_MAINTAIN_SERVICE == null) init();
        return NAMING_MAINTAIN_SERVICE;
    }

    public static RegisterService createRegisterService() throws NacosWrapperException {
        return RegisterServiceFactory.createRegisterService(getNamingService(), getNamingMaintainService());
    }

    public static DiscoveryService createDiscoveryService() throws NacosWrapperException {
        return DiscoveryServiceFactory.createDiscoveryService(getNamingService());
    }

}
