package com.anner.common.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.anner.common.demo.HelloJackson.Condition;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class HelloJacksonTest {

	private HelloJackson helloJackson;

	@Before
	public void setup() {
		this.helloJackson = new HelloJackson();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testJsonSerializeMapFail() throws JsonProcessingException {
		helloJackson.setName("name");
		JsonMapper mapper = JsonMapper.builder()
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.build();
		String value = mapper.writeValueAsString(helloJackson);
		assertEquals("{\"name\":\"name\",\"t\":\"ONE\",\"condition\":{\"when\":\"01-01-1970 12:00:00\"}}", value);
		Map<String, Object> objMap = mapper.readValue(value, LinkedHashMap.class);
		// 如果指定为 map ， 那么就会转为 kv 形式的对象
		assertEquals("name", objMap.get("name"));
		// 注意转换为了 map
		assertTrue(objMap.get("condition") instanceof LinkedHashMap);
		// rpc 场景下，可能就是定义的类型就是map ，但是内部存在复杂的对象，这个情况如何全部恢复呢?

		// map 还可以继续包装一层
		Map<HelloJackson.Type, HelloJackson> m = new HashMap<>();
		m.put(HelloJackson.Type.TWO, helloJackson);
		assertEquals("{\"TWO\":{\"name\":\"name\",\"t\":\"ONE\",\"condition\":{\"when\":\"01-01-1970 12:00:00\"}}}",
				mapper.writeValueAsString(m));
		// 创建类型引用 - typeFactory 的作用
		TypeFactory typeFactory = mapper.getTypeFactory();
		JavaType mapType = typeFactory.constructMapType(Map.class, HelloJackson.Type.class, HelloJackson.class);
		// 反序列化回Map
		Map<HelloJackson.Type, HelloJackson> deserializedMap = mapper.readValue(mapper.writeValueAsString(m), mapType);
		assertEquals("name", deserializedMap.get(HelloJackson.Type.TWO).getName());
		assertTrue(deserializedMap.get(HelloJackson.Type.TWO).getCondition() instanceof Condition);
	}
}
