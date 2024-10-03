package org.pj.config;

import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Reloadable;

@Sources("file:./config/config.properties")
public interface AppConfig extends Reloadable {
    // Redis configuration
    @Key("redis.host")
    String redisHost();

    @Key("redis.port")
    int redisPort();

    @Key("redis.pool.maxTotal")
    int redisMaxTotal();

    @Key("redis.pool.maxIdle")
    int redisMaxIdle();

    @Key("redis.pool.minIdle")
    int redisMinIdle();

    // HikariCP configuration
    @Key("db.url")
    String dbUrl();

    @Key("db.username")
    String dbUsername();

    @Key("db.password")
    String dbPassword();

    @Key("db.driverClassName")
    String dbDriverClassName();

    @Key("db.pool.maxPoolSize")
    int dbMaxPoolSize();

    @Key("db.pool.minIdle")
    int dbMinIdle();

    @Key("db.pool.idleTimeout")
    long dbIdleTimeout();

    @Key("db.pool.maxLifetime")
    long dbMaxLifetime();

    @Key("db.pool.connectionTimeout")
    long dbConnectionTimeout();
}

