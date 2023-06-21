package com.anner.naming.nacos.register.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.anner.common.collection.CollectionUtils;
import com.anner.common.log.Logger;
import com.anner.common.string.StringUtils;
import com.anner.naming.nacos.NacosManager;
import com.anner.naming.nacos.NacosProxy;
import com.anner.naming.nacos.NacosServiceInfoParser;
import com.anner.naming.nacos.bean.NacosExecutor;
import com.anner.naming.nacos.bean.NacosInstance;
import com.anner.naming.nacos.constant.NacosConstants;
import com.anner.naming.nacos.exception.NacosWrapperException;
import com.anner.naming.nacos.register.api.RegisterService;

import java.util.List;
import java.util.Map;

/**
 * Created by anner on 2023/3/23
 */
public class NacosRegisterService implements RegisterService {

    private final NamingService namingService;
    private final NamingMaintainService namingMaintainService;


    public NacosRegisterService(NamingService namingService, NamingMaintainService namingMaintainService) {
        this.namingService = namingService;
        this.namingMaintainService = namingMaintainService;
    }

    @Override
    public void register(NacosInstance nacosInstance) throws NacosWrapperException {
        String groupName = nacosInstance.getServiceDomain();
        String serviceName = nacosInstance.getServiceName();

        Service service = NacosProxy.apply(new NacosExecutor<Service>() {
            @Override
            public Service execute() throws NacosException, NacosWrapperException {
                return namingMaintainService.queryService(serviceName, groupName);
            }

            @Override
            public void onError(NacosWrapperException nacosWrapperException) {
                // ignore
            }

            @Override
            public boolean ignoreException(NacosWrapperException nacosWrapperException) {
                return nacosWrapperException.getErrCode() == NacosWrapperException.CLIENT_ERROR
                        && StringUtils.contains(nacosWrapperException.getErrMsg(), NacosConstants.Exception.SERVICE_NOT_FOUND);
            }
        });

        if (service == null) {
            registerService(serviceName, groupName);
        }

        Logger.info("service info: " + nacosInstance.getServiceName() + " existed. Will register fineInstance: " + nacosInstance + " to this.");
        registerInstance(nacosInstance);
    }

    @Override
    public void deregister(NacosInstance nacosInstance) throws NacosWrapperException {
        NacosProxy.apply(new NacosExecutor<Void>() {
            @Override
            public Void execute() throws NacosException, NacosWrapperException {
                namingService.deregisterInstance(nacosInstance.getServiceName(), nacosInstance.getServiceDomain(), buildInstance(nacosInstance));
                Logger.info("Deregister success. Instance: " + nacosInstance);
                return null;
            }

            @Override
            public void onError(NacosWrapperException nacosWrapperException) {
                Logger.error("Deregister failed. Instance: " + nacosInstance, nacosWrapperException);
            }
        });
    }

    @Override
    public void deleteService(String serviceName, String serviceDomain) throws NacosWrapperException {
        NacosProxy.apply(new NacosExecutor<Void>() {
            @Override
            public Void execute() throws NacosException, NacosWrapperException {
                // 删除服务前，需要将其名下的所有实例先依次注销掉
                List<Instance> instancesUnderService = namingService.getAllInstances(serviceName, serviceDomain);
                if (CollectionUtils.isNotEmpty(instancesUnderService)) {
                    for (Instance instance : instancesUnderService) {
                        namingService.deregisterInstance(serviceName, serviceDomain, instance);
                        Logger.info("Deregister success. Instance: " + instance);
                    }
                }
                namingMaintainService.deleteService(serviceName, serviceDomain);
                return null;
            }

            @Override
            public void onError(NacosWrapperException nacosWrapperException) {
                Logger.error("Delete service failed. Service: " + serviceName + " ServiceDomain: " + serviceDomain, nacosWrapperException);
            }
        });
    }

    @Override
    public void deleteServiceDomain(String serviceDomain) throws NacosWrapperException {
        Map<String, List<NacosInstance>> allService = NacosManager.createDiscoveryService().getAllService(serviceDomain);
        for (String serviceName : allService.keySet()) {
            deleteService(serviceName, serviceDomain);
        }
    }

    @Override
    public void updateInstance(NacosInstance nacosInstance) throws NacosWrapperException {
        NacosProxy.apply(new NacosExecutor<Void>() {
            @Override
            public Void execute() throws NacosException, NacosWrapperException {
                namingMaintainService.updateInstance(nacosInstance.getServiceName(), nacosInstance.getServiceDomain(), buildInstance(nacosInstance));
                Logger.info("Update success. Instance: " + nacosInstance);
                return null;
            }

            @Override
            public void onError(NacosWrapperException nacosWrapperException) {
                Logger.error("Update failed. Instance: " + nacosInstance, nacosWrapperException);
            }
        });
    }


    private void registerService(String serviceName, String groupName) throws NacosWrapperException {
        NacosProxy.apply(new NacosExecutor<Void>() {
            @Override
            public Void execute() throws NacosException, NacosWrapperException {
                namingMaintainService.createService(serviceName, groupName, NacosConstants.Naming.PROTECT_THRESHOLD);
                Logger.info("create service: " + serviceName + " in group: " + groupName + " success");
                return null;
            }

            @Override
            public void onError(NacosWrapperException e) {
                Logger.error("create service: " + serviceName + " serviceDomain: " + groupName + " failed. ", e);
            }
        });
    }

    private void registerInstance(NacosInstance instance) throws NacosWrapperException {
        NacosProxy.apply(new NacosExecutor<Void>() {
            @Override
            public Void execute() throws NacosException, NacosWrapperException {
                namingService.registerInstance(instance.getServiceName(), instance.getServiceDomain(), buildInstance(instance));
                Logger.info("register success. Instance: " + instance);
                return null;
            }

            @Override
            public void onError(NacosWrapperException nacosWrapperException) {
                Logger.error("register instance failed. ", nacosWrapperException);
            }
        });
    }

    private Instance buildInstance(NacosInstance nacosInstance) {
        Instance instance = new Instance();
        instance.setServiceName(NacosServiceInfoParser.buildServiceName(nacosInstance.getServiceName(), nacosInstance.getServiceDomain()));
        instance.setEphemeral(NacosConstants.Naming.EPHEMERAL);
        instance.setIp(nacosInstance.getIp());
        instance.setPort(Integer.parseInt(nacosInstance.getPort()));
        instance.setMetadata(nacosInstance.getMetaData());
        return instance;
    }
}
