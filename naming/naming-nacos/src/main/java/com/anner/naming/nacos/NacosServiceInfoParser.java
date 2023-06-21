package com.anner.naming.nacos;

import com.anner.common.string.StringUtils;
import com.anner.naming.nacos.bean.Pair;
import com.anner.naming.nacos.constant.NacosConstants;

/**
 * Created by anner on 2023/3/23
 */
public class NacosServiceInfoParser {

    public static String buildServiceName(String serviceName, String groupName) {
        return StringUtils.join(NacosConstants.Naming.SPLIT_CHAR, new String[]{groupName, serviceName});
    }

    public static Pair<String, String> parseServiceName(String serviceNameInNacos) {
        if (StringUtils.isNotEmpty(serviceNameInNacos)) {
            String[] split = serviceNameInNacos.split(NacosConstants.Naming.SPLIT_CHAR);
            if (split.length > 0) return new Pair<>(split[0], split[split.length - 1]);
        }
        throw new IllegalArgumentException("Empty or illegal service name in nacos");
    }
}
