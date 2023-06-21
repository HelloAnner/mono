package com.anner.naming.nacos.register.api;

import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.anner.naming.nacos.register.impl.NacosRegisterService;

/**
 * Created by anner on 2023/3/23
 */
public class RegisterServiceFactory {

    public static RegisterService createRegisterService(NamingService namingService,
                                                        NamingMaintainService namingMaintainService) {
        return new NacosRegisterService(namingService, namingMaintainService);
    }
}
