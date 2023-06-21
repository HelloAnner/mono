package com.anner.comm;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.anner.comm.common.CommServer;
import com.anner.comm.info.CommAttribute;
import com.anner.comm.info.CommProperties;
import com.anner.comm.info.CommPublishInfo;
import com.anner.comm.info.CommServerInfo;
import com.anner.comm.info.CommServiceInfo;
import com.anner.comm.utils.CommAnnotationUtils;

/**
 * 微服务调用注册类（服务端）
 */
public class CommPublisher {

    private static final String DEFAULT_DOMAIN = "default";

    private final String domain;
    private final List<Object> serviceList = new ArrayList<>();
    private final List<CommServiceInfo> infoList = new ArrayList<>();
    private final List<ServerItem> serverList = new ArrayList<>();

    private CommPublisher(String domain) {
        this.domain = domain;
    }

    /**
     * 创建注册类对象
     */
    public static CommPublisher create() {
        return create(DEFAULT_DOMAIN);
    }

    /**
     * 创建注册类对象
     *
     * @param domain 服务域
     */
    public static CommPublisher create(String domain) {
        return new CommPublisher(domain);
    }

    /**
     * 添加服务
     *
     * @param services 服务对象，需要实现拥有 @CommService 注解的接口
     */
    public CommPublisher addService(Object... services) {
        List<Object> sList = Arrays.asList(services);
        serviceList.addAll(sList);
        infoList.addAll(
                sList.stream()
                        .map(Object::getClass)
                        .map(CommAnnotationUtils::getServicesInfo)
                        .collect(Collectors.toList()));
        return this;
    }

    /**
     * 添加通信服务端
     *
     * @param server 通信服务端
     */
    public CommPublisher addServer(CommServer server, CommAttribute... attributes) {
        serverList.add(new ServerItem(server, attributes));
        return this;
    }

    /**
     * 将服务注册到通信服务端中
     *
     * @return 返回已注册的服务信息
     */
    public CommPublishInfo publish() {
        CommAttribute hostAttr = getHostAttribute();
        CommAttribute domainAttr = CommAttribute.domain(domain);
        List<CommServerInfo> serverInfoList = new ArrayList<>();
        serverList.forEach(serverInfo -> {
            CommServer server = serverInfo.getServer();
            serviceList.forEach(server::registerService);
            serverInfoList.add(new CommServerInfo(
                    serverInfo.mergeAttributes(server.getPublishAttributes(), hostAttr, domainAttr),
                    infoList.toArray(new CommServiceInfo[0])));
        });
        if (DEFAULT_DOMAIN.equals(domain)) {
            System.out.println(
                    "CommPublisher: specify a unique domain for the published service in production, `default` is only for TEST.");
        }
        return new CommPublishInfo(domain, serverInfoList);
    }

    /**
     * 将服务注册到通信服务端中
     *
     * @param consumer 服务注册信息处理方法
     */
    public void publish(Consumer<CommPublishInfo> consumer) {
        consumer.accept(publish());
    }

    private CommAttribute getHostAttribute() {
        List<String> ipList = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface iface = en.nextElement();
                if (iface.isUp() && !iface.isLoopback()) {
                    for (Enumeration<InetAddress> addrs = iface.getInetAddresses(); addrs.hasMoreElements();) {
                        InetAddress addr = addrs.nextElement();
                        if (addr instanceof Inet4Address) {
                            ipList.add(addr.getHostAddress());
                        }
                    }
                }
            }
            if (!ipList.isEmpty()) {
                return CommAttribute.host(String.join(",", ipList.toArray(new String[0])));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class ServerItem {
        private final CommServer server;
        private final CommAttribute[] attrs;

        public ServerItem(CommServer server, CommAttribute[] attrs) {
            this.server = server;
            this.attrs = attrs;
        }

        public CommServer getServer() {
            return server;
        }

        public CommProperties mergeAttributes(CommProperties properties, CommAttribute... otherAttrs) {
            for (CommAttribute attr : attrs) {
                properties.put(attr);
            }
            for (CommAttribute ca : otherAttrs) {
                if (!properties.containsKey(ca.getKey())) {
                    properties.put(ca);
                }
            }
            // 添加kind=fine-comm的常量值，用于在服务注册中进行标识
            properties.put(CommAttribute.kind());
            return properties;
        }
    }

}
