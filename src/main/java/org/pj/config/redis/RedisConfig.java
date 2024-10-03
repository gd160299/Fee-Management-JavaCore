package org.pj.config.redis;

import org.pj.config.AppConfig;
import org.pj.config.ConfigWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConfig {
    private static JedisPool jedisPool;
    private static final AppConfig config;
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    static {
        ConfigWatcher configWatcher = new ConfigWatcher();
        config = configWatcher.getAppConfig();
        initializePool();
    }

    private static void initializePool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(config.redisMaxTotal());
        poolConfig.setMaxIdle(config.redisMaxIdle());
        poolConfig.setMinIdle(config.redisMinIdle());
        if (jedisPool != null) {
            jedisPool.close();
        }
        jedisPool = new JedisPool(poolConfig, config.redisHost(), config.redisPort());
        logger.info("Jedis pool initialized with updated config");
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static void reloadConfig() {
        initializePool();
    }

    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
