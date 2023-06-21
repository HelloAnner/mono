package com.anner.naming.nacos.constant;

import com.anner.common.log.Logger;
import com.anner.common.string.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class NacosConstants {

    private static final String PROPERTIES_PATH = System.getProperty("user.dir") + File.separator + "SRD.properties";

    public static class Server {
        public static final String IP;
        public static final String PORT;
        public static final String USER;
        public static final String PASSWORD;

        static {
            // 优先从 "SRD.properties" 配置中获取(默认不需要配置)
            // 其次通过环境变量获取
            Properties properties = getProperties(PROPERTIES_PATH);
            String ip = properties.getProperty("SRD.IP");
            String port = properties.getProperty("SRD.PORT");
            String user = properties.getProperty("SRD.USER");
            String password = properties.getProperty("SRD.PASSWORD");
            if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(port)) {
                ip = System.getenv("NACOS_IP");
                port = System.getenv("NACOS_PORT");
            }
            if (StringUtils.isEmpty(user) || StringUtils.isEmpty(password)) {
                user = System.getenv("NACOS_USER");
                password = System.getenv("NACOS_PASSWORD");
            }
            IP = ip;
            PORT = port;
            USER = user;
            PASSWORD = password;
        }


    }

    public static class Naming {
        public static final String NAMESPACE_ID;
        public static final String SPLIT_CHAR = "@@";
        public static final float PROTECT_THRESHOLD;
        public static final boolean EPHEMERAL = false;

        static {
            Properties properties = getProperties(PROPERTIES_PATH);
            String namespaceId = properties.getProperty("SRD.NAMING.NAMESPACE");
            String protectThreshold = properties.getProperty("SRD.NAMING.SERVICE.PROTECT_THRESHOLD");
            if (StringUtils.isEmpty(namespaceId)) {
                namespaceId = System.getenv("NACOS_NAMING_NAMESPACE");
            }
            if (protectThreshold != null) {
                PROTECT_THRESHOLD = Float.parseFloat(protectThreshold);
            } else {
                PROTECT_THRESHOLD = 0;
            }
            NAMESPACE_ID = namespaceId;
        }
    }

    public static class Exception {
        public static final String SERVICE_NOT_FOUND = "service not found";
    }

    private static Properties getProperties(String path) {
        Properties properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            properties.load(fis);
        } catch (java.lang.Exception e) {
            Logger.error("Read properties error", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return properties;
    }
}
