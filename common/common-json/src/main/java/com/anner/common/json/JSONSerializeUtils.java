package com.anner.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by anner on 2023/3/21
 */
public class JSONSerializeUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 注册自定义序列化器模块
     *
     * @param module 序列化器模块
     */
    public static void registerSerializerModule(Module module) {
        MAPPER.registerModule(module);
    }

    /**
     * 序列化对象
     *
     * @param o 被序列化对象
     * @return json字符串
     */
    public static String serialize(Object o) throws JsonProcessingException {
        return MAPPER.writeValueAsString(o);
    }

    /**
     * 反序列化对象
     *
     * @param jsonStr json字符串
     * @param cls     反序列化的类型
     * @return 反序列化结果对象
     */
    public static <T> T deserialize(String jsonStr, Class<T> cls) throws JsonProcessingException {
        return MAPPER.readValue(jsonStr, cls);
    }
}
