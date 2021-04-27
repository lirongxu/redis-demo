package com.lemon.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author lemon
 * @Date 2021/4/25
 */
@Configuration
@Import(RedisProperties.class)
public class RedissonConfig {

    /**
     * 单节点模式
     * @param redisProperties
     * @return
     */
    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties){
        Config config = new Config();
        Codec codec = new StringCodec();
        config.setCodec(codec);

        config.useSingleServer()
                .setAddress(redisProperties.getSingle().getUrl().startsWith("redis") ? redisProperties.getSingle().getUrl():String.format("redis://%s", redisProperties.getSingle().getUrl()))
                .setPassword(redisProperties.getSingle().getPassword())
                .setDatabase(redisProperties.getSingle().getDatabase())
                .setTimeout(redisProperties.getSingle().getTimeout());

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    /**
     * 主从模式
     * 一主两从
     * @param redisProperties
     * @return
     */
//    @Bean(name = "RedissonClient")
    public RedissonClient getRedissonClient(RedisProperties redisProperties){
        Config config = new Config();
        Codec codec = new StringCodec();
        config.setCodec(codec);

        String[] slave = redisProperties.getMasterSlave().getSlave().split(",");
        config.useMasterSlaveServers()
                .setMasterAddress(redisProperties.getMasterSlave().getMaster().startsWith("redis")?redisProperties.getMasterSlave().getMaster():String.format("redis://%s", redisProperties.getMasterSlave().getMaster()))
                .addSlaveAddress(slave[0].startsWith("redis") ? slave[0]: String.format("redis://%s", slave[0]), slave[1].startsWith("redis") ? slave[1]: String.format("redis://%s", slave[1]))
                .setPassword(redisProperties.getMasterSlave().getPassword())
                .setDatabase(redisProperties.getMasterSlave().getDatabase())
                .setTimeout(redisProperties.getMasterSlave().getTimeout());

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    /**
     * 哨兵摸模式
     * @param redisProperties
     * @return
     */
//    @Bean(name = "SentinelRedissonClient")
    public RedissonClient getRedissonSentinelClient(RedisProperties redisProperties){
        Config config = new Config();
        Codec codec = new StringCodec();
        config.setCodec(codec);

        String[] url = redisProperties.getSentinel().getSentinelUrl().split(",");
        config.useSentinelServers()
                .setMasterName(redisProperties.getSentinel().getMasterName())
                .addSentinelAddress(url[0].startsWith("redis") ? url[0]: String.format("redis://%s", url[0]))
                .addSentinelAddress(url[1].startsWith("redis") ? url[1]: String.format("redis://%s", url[1]))
                .addSentinelAddress(url[2].startsWith("redis") ? url[2]: String.format("redis://%s", url[2]))
                .setSentinelPassword(redisProperties.getSentinel().getPassword())
                .setTimeout(redisProperties.getSentinel().getTimeout());

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
