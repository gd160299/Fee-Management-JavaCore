package org.pj.config.redis;

import org.aeonbits.owner.ConfigFactory;
import org.pj.config.AppConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConfig {
    private static final JedisPool jedisPool;

    static {
        AppConfig config = ConfigFactory.create(AppConfig.class);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.redisMaxTotal()); // Số kết nối tối đa
        poolConfig.setMaxIdle(config.redisMaxIdle());   // Số kết nối nhàn rỗi tối đa
        poolConfig.setMinIdle(config.redisMinIdle());   // Số kết nối nhàn rỗi tối thiểu
        jedisPool = new JedisPool(poolConfig, config.redisHost(), config.redisPort());
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
