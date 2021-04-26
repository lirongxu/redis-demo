## redis

> redis哨兵集群搭建

编写docker-compose资源清单：
1主2从  3哨兵
```
version: '3'
services:
  master:
    image: redis:6
    container_name: redis-master
    ports:
      - "6379:6379"
    command: redis-server /usr/local/etc/redis/redis.conf
    volumes:
    - ./redis-master.conf:/usr/local/etc/redis/redis.conf
    - ./data/master:/data
  slave1:
    image: redis:6
    container_name: redis-slave-1
    ports:
      - "6380:6380"
    volumes:
    - ./data/slave1:/data
    - ./redis-slave1.conf:/usr/local/etc/redis/redis.conf
    command: redis-server /usr/local/etc/redis/redis.conf
    depends_on:
    - master
  slave2:
    image: redis:6
    container_name: redis-slave-2
    ports:
      - "6381:6381"
    volumes:
    - ./data/slave2:/data
    - ./redis-slave2.conf:/usr/local/etc/redis/redis.conf
    command: redis-server /usr/local/etc/redis/redis.conf
    depends_on:
    - master
  sentinel1:
    image: redis:6
    container_name: redis-sentinel-1
    ports:
      - "26379:26379"
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
    - "./sentinel1.conf:/usr/local/etc/redis/sentinel.conf"
  sentinel2:
    image: redis:6
    container_name: redis-sentinel-2
    ports:
      - "26380:26380"
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
    - "./sentinel2.conf:/usr/local/etc/redis/sentinel.conf"
  sentinel3:
    image: redis:6
    container_name: redis-sentinel-3
    ports:
      - "26381:26381"
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
    - ./sentinel3.conf:/usr/local/etc/redis/sentinel.conf
```
对应的配置文件如下：
```
//redis-master.conf
port 6379
# 设定密码认证
requirepass 123456
masterauth 123456

//redis-slave1.conf
port 6380
requirepass 123456
slaveof 10.3.16.133 6379
masterauth 123456

//redis-slave2.conf
port 6381
requirepass 123456
slaveof 10.3.16.133 6379
masterauth 123456

//sentinel1.conf
port 26379
dir "/data"
# monitor代表监控 mymaster指服务器名称 后面是master的ip 端口 后面是qurom指至少2个哨兵认为主服务不可用时才failover
sentinel monitor mymaster 10.3.16.133 6379 2
sentinel auth-pass mymaster 123456
# 判断主节点时间
sentinel down-after-milliseconds mymaster 30000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 100000
sentinel deny-scripts-reconfig yes

//sentinel2.conf
port 26380
dir "/data"
sentinel monitor mymaster 10.3.16.133 6379 2
sentinel auth-pass mymaster 123456
sentinel down-after-milliseconds mymaster 30000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 100000
sentinel deny-scripts-reconfig yes

//sentinel3.conf
port 26381
dir "/data"
sentinel monitor mymaster 10.3.16.133 6379 2
sentinel auth-pass mymaster 123456
sentinel down-after-milliseconds mymaster 30000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 100000
sentinel deny-scripts-reconfig yes
```
