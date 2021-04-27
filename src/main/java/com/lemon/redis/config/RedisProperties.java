package com.lemon.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author lemon
 * @Date 2021/4/25
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
    /**
     * 单节点模式
     */
    private Single Single;

    /**
     * 主从模式
     */
    private MasterSlave masterSlave;

    /**
     * 哨兵模式
     */
    private Sentinel sentinel;

    @Data
    static class Sentinel {

        private String masterName;

        private String sentinelUrl;

        private String password;

        private Integer database;

        private Integer timeout;
        //最大空闲连接数
        private Integer maxIdle;
        //最大连接数
        private Integer maxTotal;
        //最大等待时间
        private Integer maxWaitMillis;
    }

    @Data
    static class Single {
        private String url;

        private String password;

        private Integer database;

        private Integer timeout;
    }

    @Data
    static class MasterSlave {
        private String master;

        private String slave;

        private String password;

        private Integer database;

        private Integer timeout;
    }
}
