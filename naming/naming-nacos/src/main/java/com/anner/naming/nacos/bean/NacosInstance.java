package com.anner.naming.nacos.bean;

import com.anner.naming.nacos.discovery.api.DiscoveryService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by anner on 2023/3/22
 */
public class NacosInstance {
    /**
     * 服务域名 (域名 1:N 服务名)
     * 命名规则: 不能包含字符"@@", 域名不可重复，同一域名下的服务名不可重复
     */
    private String serviceDomain;

    /**
     * 服务名
     * 命名规则: 不能包含字符"@@", 同一域名下的服务名不可重复
     */
    private String serviceName;

    private String ip;
    private String port;
    /**
     * 实例的是否健康
     * 注意不具备实时性，要实时判断当前实例是否健康 请使用
     * {@link DiscoveryService#isCurrentHealthy(FineInstance)}
     */
    private boolean healthy;
    private Map<String, String> metaData = new HashMap<>();

    private NacosInstance(String serviceDomain, String serviceName, String ip, String port, boolean healthy,
                          Map<String, String> metaData) {
        this.serviceDomain = serviceDomain;
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
        this.healthy = healthy;
        if (metaData != null) {
            this.metaData = metaData;
        }
    }

    public String getServiceDomain() {
        return serviceDomain;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof NacosInstance
                && Objects.equals(serviceDomain, ((NacosInstance) o).getServiceDomain())
                && Objects.equals(serviceName, ((NacosInstance) o).getServiceName())
                && Objects.equals(ip, ((NacosInstance) o).getIp())
                && Objects.equals(port, ((NacosInstance) o).getPort())
                && Objects.equals(healthy, ((NacosInstance) o).isHealthy())
                && Objects.equals(metaData, ((NacosInstance) o).getMetaData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceDomain, serviceName, ip, port, healthy, metaData);
    }

    @Override
    public String toString() {
        return "FineInstance{" +
                "serviceDomain='" + serviceDomain + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", healthy=" + healthy +
                ", metaData=" + metaData +
                '}';
    }

    public static class Builder {
        private String serviceDomain;
        private String serviceName;
        private String ip;
        private String port;
        private boolean healthy = true;
        private Map<String, String> metaData = new HashMap<>();

        public Builder serviceDomain(String serviceDomain) {
            this.serviceDomain = serviceDomain;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder port(String port) {
            this.port = port;
            return this;
        }

        /**
         * healthy默认为true  只有需要显式指定不健康的时候才需要调用
         */
        public Builder unHealthy() {
            this.healthy = false;
            return this;
        }

        public Builder addMetaData(String key, String value) {
            metaData.put(key, value);
            return this;
        }

        public Builder metaData(Map<String, String> metaData) {
            this.metaData = metaData;
            return this;
        }

        public Builder fromInstance(NacosInstance instance) {
            this.serviceDomain = instance.serviceDomain;
            this.serviceName = instance.getServiceName();
            this.ip = instance.getIp();
            this.port = instance.getPort();
            this.healthy = instance.isHealthy();
            this.metaData = instance.getMetaData();
            return this;
        }

        public NacosInstance build() {
            return new NacosInstance(serviceDomain, serviceName, ip, port, healthy, metaData);
        }
    }
}
