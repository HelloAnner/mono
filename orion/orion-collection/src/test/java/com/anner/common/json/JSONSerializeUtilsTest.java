package com.anner.common.json;

import com.anner.common.reflect.Reflect;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class JSONSerializeUtilsTest {

    @Test
    public void base() throws JsonProcessingException {
        BaseData data = new BaseData();
        data.setIntValue(123);
        data.setStrValue("hello");
        data.setDateValue(new Date(1662695373));

        String jsonStr = JSONSerializeUtils.serialize(data);

        BaseData data2 = JSONSerializeUtils.deserialize(jsonStr, BaseData.class);
        assertEquals(123, data2.getIntValue());
        assertEquals("hello", data2.getStrValue());
        assertEquals(new Date(1662695373), data2.getDateValue());
    }

    @Test
    public void custom() throws Exception {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ByteArrayInputStream.class, new ByteArrayInputStreamSerializer());
        module.addDeserializer(ByteArrayInputStream.class, new ByteArrayInputStreamDeserializer());
        JSONSerializeUtils.registerSerializerModule(module);

        ByteArrayInputStream in = new ByteArrayInputStream("hello world / 你好，世界".getBytes(StandardCharsets.UTF_8));
        CustomData data = new CustomData();
        data.setInputStream(in);
        data.setFilename("hello.txt");

        String jsonStr = JSONSerializeUtils.serialize(data);

        CustomData data2 = JSONSerializeUtils.deserialize(jsonStr, CustomData.class);

        assertEquals(data.getFilename(), data2.getFilename());
        assertEquals("hello world / 你好，世界", IOUtils.toString(data2.getInputStream(), StandardCharsets.UTF_8));
    }

    public static class BaseData {
        private int intValue;
        private String strValue;
        private Date dateValue;

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public String getStrValue() {
            return strValue;
        }

        public void setStrValue(String strValue) {
            this.strValue = strValue;
        }

        public Date getDateValue() {
            return dateValue;
        }

        public void setDateValue(Date dateValue) {
            this.dateValue = dateValue;
        }
    }

    public static class CustomData {
        private ByteArrayInputStream inputStream;
        private String filename;

        public ByteArrayInputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(ByteArrayInputStream inputStream) {
            this.inputStream = inputStream;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }

    private static class ByteArrayInputStreamSerializer extends JsonSerializer<ByteArrayInputStream> {
        @Override
        public void serialize(ByteArrayInputStream byteArrayInputStream, JsonGenerator jsonGenerator,
                SerializerProvider serializerProvider) throws IOException {
            byte[] bs = Reflect.on(byteArrayInputStream).get("buf");
            jsonGenerator.writeString(Base64.getEncoder().encodeToString(bs));
        }
    }

    private static class ByteArrayInputStreamDeserializer extends JsonDeserializer<ByteArrayInputStream> {
        @Override
        public ByteArrayInputStream deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException, JacksonException {
            String s = jsonParser.getValueAsString();
            return new ByteArrayInputStream(Base64.getDecoder().decode(s));
        }
    }
}