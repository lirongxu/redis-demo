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
@Import(RedissonProperties.class)
public class RedissonConfig {

    @Bean(name = "RedissonClient")
    public RedissonClient getRedissonClient(RedissonProperties redissonProperties){
        Config config = new Config();
        Codec codec = new StringCodec();
        config.setCodec(codec);

        String[] slave = redissonProperties.getMasterSlave().getSlave().split(",");
        config.useMasterSlaveServers()
                .setMasterAddress(redissonProperties.getMasterSlave().getMaster())
                .addSlaveAddress(slave[0], slave[1])
                .setPassword(redissonProperties.getMasterSlave().getPassword())
                .setDatabase(redissonProperties.getMasterSlave().getDatabase())
                .setTimeout(redissonProperties.getMasterSlave().getTimeout());

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    @Bean(name = "SingleRedissonClient")
    public RedissonClient getRedissonSingleClient(RedissonProperties redissonProperties){
        Config config = new Config();
        Codec codec = new StringCodec();
        config.setCodec(codec);

        config.useSingleServer()
                .setAddress(redissonProperties.getSingle().getUrl())
                .setPassword(redissonProperties.getSingle().getPassword())
                .setDatabase(redissonProperties.getSingle().getDatabase())
                .setTimeout(redissonProperties.getSingle().getTimeout());

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    @Bean(name = "SentinelRedissonClient")
    public RedissonClient getRedissonSentinelClient(RedissonProperties redissonProperties){
        Config config = new Config();
        Codec codec = new StringCodec();
        config.setCodec(codec);

        String[] url = redissonProperties.getSentinel().getSentinelUrl().split(",");
        config.useSentinelServers()
                .setMasterName(redissonProperties.getSentinel().getMasterName())
                .addSentinelAddress(url[0])
                .setSentinelPassword(redissonProperties.getSentinel().getPassword())
                .setTimeout(redissonProperties.getSentinel().getTimeout());

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
