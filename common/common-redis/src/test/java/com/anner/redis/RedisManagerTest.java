package com.anner.redis;


import org.junit.Assert;
import org.junit.Test;


public class RedisManagerTest {

    @Test
    public void testInit() {
        RedisManager.init("10.211.55.4", 6379, "Anner_login_123");
        RedisClient client = RedisManager.apply();
        client.set("name", "test");
        Assert.assertEquals("test", client.get("name"));

        RedisManager.init("10.211.55.4", 16379, "Anner_login_123");
        Assert.assertNull(RedisManager.apply());

        RedisManager.init("10.211.55.5", 6379, "Anner_login_123");
        Assert.assertNull(RedisManager.apply());

        RedisManager.init("10.211.55.4", 6379, "Anner_login_1234");
        Assert.assertNull(RedisManager.apply());
    }
}