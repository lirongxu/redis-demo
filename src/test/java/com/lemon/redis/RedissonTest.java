package com.lemon.redis;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author lemon
 * @Date 2021/4/27
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;

    @Test
    public void testString() {
        RBucket<String> rBucket = redissonClient.getBucket("key1", new StringCodec());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "zhangsan");
        jsonObject.put("age", 20);
        jsonObject.put("score", 60);
        //set 数据
        rBucket.set(jsonObject.toJSONString());
        String value = rBucket.get();
        log.info("string value:{}", value);

        RAtomicLong atomicLong = redissonClient.getAtomicLong("key2");
        atomicLong.set(1);
        log.info("long value:{}", atomicLong.incrementAndGet());
    }

    @Test
    public void testList() {
        RList<String> rList = redissonClient.getList("list1", new StringCodec());
        rList.add("java");
        rList.add("c#");
        List<String> result = rList.get(0, 1);
        log.info("list value:{}", result);
    }

    @Test
    public void testSet() {
        RSet<String> rSet = redissonClient.getSet("set1", new StringCodec());
        rSet.add("java");
        rSet.add("java");
        rSet.add("C#");

        Set<String> result = rSet.readAll();
        log.info("set value:{}", result);
    }

    @Test
    public void testZSet() {
        RSortedSet<String> rSortedSet = redissonClient.getSortedSet("zset1", new StringCodec());
        rSortedSet.add("5");
        rSortedSet.add("6");
        rSortedSet.add("java");
        rSortedSet.add("1");
        rSortedSet.add("3");

        Collection<String> result = rSortedSet.readAll();
        log.info("zset value:{}", result);
    }

    @Test
    public void testHash() {
        RMap<String, String> rMap = redissonClient.getMap("map1");
        rMap.put("db", "mysql");
        rMap.put("cache", "redis");
        rMap.put("lan", "java");
        rMap.put("lan", "java");

        rMap.forEach((key, value) -> {
            log.info("map values:{} {}", key, value);
        });
    }

    @Test
    public void testHyperLogLog() {
        RHyperLogLog<String> rHyperLogLog = redissonClient.getHyperLogLog("hyperloglog1", new StringCodec());
        rHyperLogLog.add("foo");
        rHyperLogLog.add("a");
        rHyperLogLog.add("o");

        RHyperLogLog<String> rHyperLogLog2 = redissonClient.getHyperLogLog("hyperloglog2", new StringCodec());
        rHyperLogLog2.add("red");
        rHyperLogLog2.add("white");
        rHyperLogLog.mergeWith("rHyperLogLog2");
    }

    @Test
    public void testBloomFilter() {
        RBloomFilter<String> rBloomFilter = redissonClient.getBloomFilter("bloomFilter1");
        //元素数量 误差率
        rBloomFilter.tryInit(55000000L, 0.03);
        rBloomFilter.add("java");
        rBloomFilter.add("C#");
        boolean result = rBloomFilter.contains("java");
        System.out.println(result);

        RBloomFilter<SimpleObj> rBloomFilter2 = redissonClient.getBloomFilter("bloomFilter2");
        //元素数量 误差率
        rBloomFilter2.tryInit(55000000L, 0.03);
        rBloomFilter2.add(new SimpleObj("field1Value", "field2Value"));
        rBloomFilter2.add(new SimpleObj("field5Value", "field8Value"));
        boolean result2 = rBloomFilter2.contains(new SimpleObj("field1Value", "field2Value"));
        System.out.println(result2);
    }

    @Data
    @AllArgsConstructor
    public class SimpleObj {
        private String v1;
        private String v2;
    }

    @Test
    public void testGeoHash() {
        RGeo<String> rGeo = redissonClient.getGeo("geo1", new StringCodec());
        rGeo.add(116.23128, 40.22077, "beijing");
        rGeo.add(121.48941, 31.40527, "shanghai");

        Double km = rGeo.dist("beijing", "shanghai", GeoUnit.KILOMETERS);
        log.info("北京到上海距离:{}km", km);
    }

    /**
     * 队列
     */
    @Test
    public void queue() {
        RQueue<String> queue = redissonClient.getQueue("queue");
        queue.add("java");
        queue.add("c#");
        queue.add("js");
        System.out.println(queue.poll());
        System.out.println(queue.poll());
    }

    /**
     * 限流场景 令牌桶
     * @throws InterruptedException
     */
    @Test
    public void rateLimiter() throws InterruptedException {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter("rateLimiter1");
        //初始化 最大流速:每1秒钟产生5个令牌
        rateLimiter.trySetRate(RateType.OVERALL, 5, 1, RateIntervalUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                int i = 0;
                @Override
                public void run() {
                    while(true) {
                        rateLimiter.acquire(1);
                        System.out.println(Thread.currentThread() + "-" + System.currentTimeMillis() + "-" + i++);
                    }
                }
            }).start();
        }

        Thread.sleep(1000 * 5);
    }

    /**
     * 可重入锁
     * @throws InterruptedException
     */
    @Test
    public void lock() throws InterruptedException {
        RLock lock = redissonClient.getLock("lock");
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                lock.lock(30, TimeUnit.SECONDS);
                try {
                    System.out.println(Thread.currentThread() + "-" + System.currentTimeMillis() + "-" + "获取了锁");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }).start();
        }
        Thread.sleep(1000 * 5);
    }
    
}
