package com.anner.naming.nacos.discovery.api;

import com.anner.naming.nacos.bean.NacosInstance;
import com.anner.naming.nacos.exception.NacosWrapperException;
import com.anner.naming.nacos.register.api.RegisterService;

import java.util.List;
import java.util.Map;

/**
 * Created by anner on 2023/3/22
 */
public interface DiscoveryService {
    /**
     * 获取指定域名下所有注册的服务及其名下的实例
     *
     * @return Map<服务名, 实例列表> 注意服务名就是注册时的名字 {@link RegisterService#register(NacosInstance)}
     * @throws NacosWrapperException SRD exception
     */
    Map<String, List<NacosInstance>> getAllService(String serviceDomain) throws NacosWrapperException;

    /**
     * 获取指定域名和服务下包含的所有实例(不区分健康/可用状态)
     *
     * @param serviceName   注册时的服务名 {@link RegisterService#register(NacosInstance)}
     * @param serviceDomain 服务域名
     * @return List<NacosInstance>
     * @throws NacosWrapperException SRD exception
     */
    List<NacosInstance> getAllInstances(String serviceName, String serviceDomain) throws NacosWrapperException;

    /**
     * 获取指定域名和服务下包含的所有健康可用实例
     * 优先获取服务缓存内数据 所以会存在健康状态的延迟性
     *
     * @param serviceName   注册时的服务名 {@link RegisterService#register(NacosInstance)}
     * @param serviceDomain 服务域名
     * @return List<NacosInstance>
     * @throws NacosWrapperException SRD exception
     */
    List<NacosInstance> selectAllHealthyInstances(String serviceName, String serviceDomain) throws NacosWrapperException;


    /**
     * 获取指定域名和服务下的一个健康可用实例
     * 调用者不需要关注负载均衡问题，默认使用随机策略做负载均衡
     * <p>
     * 优先获取注册中心最新实例数据，最大程度保证了获取实例的健康状态实时性
     *
     * @param serviceName   注册时的服务名 {@link RegisterService#register(NacosInstance)}
     * @param serviceDomain 服务域名
     * @return NacosInstance
     * @throws NacosWrapperException SRD exception
     */
    NacosInstance selectOneHealthyInstance(String serviceName, String serviceDomain) throws NacosWrapperException;

    /**
     * 订阅【指定域名和服务】下的变动（服务名、实例）
     *
     * @param serviceName     要订阅的服务名 (对应注册时的服务名 {@link RegisterService#register(NacosInstance)})
     * @param serviceDomain   服务域名
     * @param serviceListener {@link ServiceListener} 响应数据变动
     * @throws NacosWrapperException SRD exception
     */
    void subscribe(String serviceName, String serviceDomain, ServiceListener serviceListener) throws NacosWrapperException;

    /**
     * 订阅指定域名下【所有服务】变动(服务名、实例)
     *
     * @param serviceDomain   服务域名
     * @param serviceListener {@link ServiceListener} 响应数据变动
     * @throws NacosWrapperException SRD exception
     */
    void subscribe(String serviceDomain, ServiceListener serviceListener) throws NacosWrapperException;

    /**
     * 退订【指定服务】
     *
     * @param serviceName     要退订的服务名
     * @param serviceDomain   服务域名
     * @param serviceListener {@link ServiceListener} 注意这里要传入订阅时的监听器
     * @throws NacosWrapperException SRD exception
     */
    void unsubscribe(String serviceName, String serviceDomain, ServiceListener serviceListener) throws NacosWrapperException;

    /**
     * 退订【所有域名下服务】
     *
     * @param serviceDomain   服务域名
     * @param serviceListener {@link ServiceListener} 注意这里要传入订阅时的监听器
     * @throws NacosWrapperException SRD exception
     */
    void unsubscribe(String serviceDomain, ServiceListener serviceListener) throws NacosWrapperException;

    /**
     * 判断指定实例的 "当前的"健康状态
     *
     * @param NacosInstance 通过{@link DiscoveryService}获取到的NacosInstance
     * @return true: 健康; false: 不健康
     * @throws NacosWrapperException SRD exception
     */
    boolean isCurrentHealthy(NacosInstance NacosInstance) throws NacosWrapperException;


}
