package com.lemon.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author lemon
 * @Date 2021/4/25
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class JedisTest {
    @Resource
    private JedisSentinelPool jedisSentinelPool;

    @Test
    public void testString() {
        Jedis jedis = jedisSentinelPool.getResource();

        String value = jedis.get("key1");
        log.info("string value:{}", value);
    }

    @Test
    public void testList() {
        Jedis jedis = jedisSentinelPool.getResource();

        List<String> value = jedis.lrange("list1",0,1);
        log.info("list value:{}", value);
    }
}
