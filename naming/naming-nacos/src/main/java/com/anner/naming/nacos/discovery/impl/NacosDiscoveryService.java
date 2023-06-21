package com.anner.naming.nacos.discovery.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.anner.common.collection.CollectionUtils;
import com.anner.common.log.Logger;
import com.anner.naming.nacos.NacosProxy;
import com.anner.naming.nacos.NacosServiceInfoParser;
import com.anner.naming.nacos.bean.NacosExecutor;
import com.anner.naming.nacos.bean.NacosInstance;
import com.anner.naming.nacos.bean.Pair;
import com.anner.naming.nacos.discovery.api.DiscoveryService;
import com.anner.naming.nacos.discovery.api.ServiceListener;
import com.anner.naming.nacos.exception.NacosWrapperException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by anner on 2023/3/23
 */
public class NacosDiscoveryService implements DiscoveryService {

    private final NamingService namingService;

    public NacosDiscoveryService(NamingService namingService) {
        this.namingService = namingService;
    }

    @Override
    public Map<String, List<NacosInstance>> getAllService(String serviceDomain) throws NacosWrapperException {

        return null;
    }

    @Override
    public List<NacosInstance> getAllInstances(String serviceName, String serviceDomain) throws NacosWrapperException {
        return null;
    }

    @Override
    public List<NacosInstance> selectAllHealthyInstances(String serviceName, String serviceDomain) throws NacosWrapperException {
        return NacosProxy.apply(new NacosExecutor<List<NacosInstance>>() {
            @Override
            public List<NacosInstance> execute() throws NacosException, NacosWrapperException {
                List<Instance> instances = namingService.selectInstances(serviceName, serviceDomain, true);
                if (CollectionUtils.isNotEmpty(instances)) {
                    List<NacosInstance> nacosInstanceList = instances.stream().map(instance -> buildNacosInstance(instance)).collect(Collectors.toList());
                    Logger.info("Select all healthy instances success. Instances count: " + nacosInstanceList.size() + " Service: " + serviceName + " Domain: " + serviceDomain);
                    return nacosInstanceList;
                }
                return Collections.emptyList();
            }

            @Override
            public void onError(NacosWrapperException nacosWrapperException) {
                Logger.error("Select all healthy instances failed. Service: " + serviceName + " Domain: " + serviceDomain, nacosWrapperException);
            }
        });
    }

    @Override
    public NacosInstance selectOneHealthyInstance(String serviceName, String serviceDomain) throws NacosWrapperException {

        return NacosProxy.apply(new NacosExecutor<NacosInstance>() {
            @Override
            public NacosInstance execute() throws NacosException, NacosWrapperException {
                // 注意这里为了保证获取到的实例一定是健康的，需要绕开本地缓存，即需要加一下参数"subscribe"为false
                Instance instance = namingService.selectOneHealthyInstance(serviceName, serviceDomain, false);
                Logger.info("Select one healthy instance success. Service: " + serviceName + " Domain: " + serviceDomain);
                return buildNacosInstance(instance);
            }

            @Override
            public void onError(NacosWrapperException nacosWrapperException) {
                Logger.error("Select all healthy instances failed. Service: " + serviceName + " Domain: " + serviceDomain, nacosWrapperException);
            }
        });
    }

    @Override
    public void subscribe(String serviceName, String serviceDomain, ServiceListener serviceListener) throws NacosWrapperException {
        NacosProxy.apply(new NacosExecutor<Void>() {
            @Override
            public Void execute() throws NacosException, NacosWrapperException {
                return null;
            }

            @Override
            public void onError(NacosWrapperException nacosWrapperException) {

            }
        });
    }

    @Override
    public void subscribe(String serviceDomain, ServiceListener serviceListener) throws NacosWrapperException {

    }

    @Override
    public void unsubscribe(String serviceName, String serviceDomain, ServiceListener serviceListener) throws NacosWrapperException {

    }

    @Override
    public void unsubscribe(String serviceDomain, ServiceListener serviceListener) throws NacosWrapperException {

    }

    @Override
    public boolean isCurrentHealthy(NacosInstance NacosInstance) throws NacosWrapperException {
        return false;
    }

    private NacosInstance buildNacosInstance(Instance instance) {
        Pair<String, String> pair = NacosServiceInfoParser.parseServiceName(instance.getServiceName());
        NacosInstance.Builder builder = new NacosInstance.Builder()
                .serviceDomain(pair.getFirst())
                .serviceName(pair.getSecond())
                .ip(instance.getIp())
                .port(String.valueOf(instance.getPort()))
                .metaData(instance.getMetadata());
        return instance.isHealthy() ? builder.build() : builder.unHealthy().build();

    }
}
