package com.lemon.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author lemon
 * @Date 2021/4/25
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StringTest {
    @Resource(name = "RedissonClient")
    private RedissonClient redissonClient;

    @Test
    public void test() {

        RBucket<Object> rBucket = redissonClient.getBucket("key1");
        System.out.println(rBucket.get());
    }
}
