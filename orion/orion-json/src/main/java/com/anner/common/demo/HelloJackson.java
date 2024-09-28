package com.anner.common.demo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 这是一个常见的组装对象
 * 可以看到，如果不指定一个专门的class type ， 是无法正确反序列化的，见单元测试
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloJackson {
    private String name;
    private Type t = Type.ONE;
    private Condition condition = new Condition();

    enum Type {
        ONE, TWO;

        private int key;
    }

    @Data
    class Condition {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        private Date when = new Date(0);
        private String where;
        private String who;
    }
}
