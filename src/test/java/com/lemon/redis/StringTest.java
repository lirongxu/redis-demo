package com.lemon.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import javax.annotation.Resource;

/**
 * @Author lemon
 * @Date 2021/4/25
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StringTest {
    @Resource
    private JedisSentinelPool jedisSentinelPool;

    @Test
    public void testJedis() {
        Jedis jedis = jedisSentinelPool.getResource();
        String value = jedis.get("key1");
        System.out.println(value);
    }
}
