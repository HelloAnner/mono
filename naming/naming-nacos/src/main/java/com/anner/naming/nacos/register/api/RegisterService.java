package com.anner.naming.nacos.register.api;

import com.anner.naming.nacos.bean.NacosInstance;
import com.anner.naming.nacos.exception.NacosWrapperException;

/**
 * Created by anner on 2023/3/22
 */
public interface RegisterService {
    /**
     * 以"实例"的粒度注册服务
     * 不需要显式的先注册"服务",再注册实例
     *
     * @param nacosInstance {@link NacosInstance}
     * @throws NacosWrapperException SRD exception
     */
    void register(NacosInstance nacosInstance) throws NacosWrapperException;

    /**
     * 注销指定实例
     *
     * @param nacosInstance {@link NacosInstance}
     * @throws NacosWrapperException
     */
    void deregister(NacosInstance nacosInstance) throws NacosWrapperException;

    /**
     * 删除整个服务
     * 注意 删除服务前,会依次注销该服务下的每个实例
     *
     * @param serviceName   serviceName
     * @param serviceDomain serviceDomain
     * @throws NacosWrapperException SRD exception
     */
    void deleteService(String serviceName, String serviceDomain) throws NacosWrapperException;

    /**
     * 删除整个服务域
     *
     * @param serviceDomain serviceDomain
     * @throws NacosWrapperException
     */
    void deleteServiceDomain(String serviceDomain) throws NacosWrapperException;

    /**
     * 更新实例信息
     * 注意:
     * 1.无法修改ip端口
     * 2.无法改变实例所属的服务
     * <p>
     * 可用于实例不下线的情况下，修改元数据。
     * 需要修改服务、实例，可以先删除/注销，再注册
     *
     * @param NacosInstance
     * @throws NacosWrapperException
     */
    void updateInstance(NacosInstance nacosInstance) throws NacosWrapperException;
}
