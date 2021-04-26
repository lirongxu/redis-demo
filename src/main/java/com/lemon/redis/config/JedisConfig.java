package com.lemon.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author lemon
 * @Date 2021/4/26
 */
@Configuration
@Import(RedisProperties.class)
public class JedisConfig {
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //最大空闲连接数
        jedisPoolConfig.setMaxIdle(10);
        //最大连接数
        jedisPoolConfig.setMaxTotal(10000);
        //当池内没有可用连接时，最大等待时间
        jedisPoolConfig.setMaxWaitMillis(10000);
        return jedisPoolConfig;
    }


    @Bean
    public JedisSentinelPool jedisSentinelPool(RedisProperties redisProperties, JedisPoolConfig jedisPoolConfig) {
        Set<String> sentinels = new HashSet<>();
        String[] slave = redisProperties.getSentinel().getSentinelUrl().split(",");
        sentinels.add(slave[0]);
        sentinels.add(slave[1]);
        sentinels.add(slave[2]);
        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(
                redisProperties.getSentinel().getMasterName(),
                sentinels,
                jedisPoolConfig,
                redisProperties.getSentinel().getPassword()
        );
        return jedisSentinelPool;
    }
}
