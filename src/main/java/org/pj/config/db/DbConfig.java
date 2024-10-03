package org.pj.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.pj.config.AppConfig;
import org.pj.config.ConfigWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DbConfig {
    private static HikariDataSource dataSource;
    private static final AppConfig config;
    private static final Logger logger = LoggerFactory.getLogger(DbConfig.class);

    static {
        ConfigWatcher configWatcher = new ConfigWatcher();
        config = configWatcher.getAppConfig();
        initializePool();
    }

    private static void initializePool() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.dbUrl());
        hikariConfig.setUsername(config.dbUsername());
        hikariConfig.setPassword(config.dbPassword());
        hikariConfig.setDriverClassName(config.dbDriverClassName());
        hikariConfig.setMaximumPoolSize(config.dbMaxPoolSize());
        hikariConfig.setMinimumIdle(config.dbMinIdle());
        hikariConfig.setIdleTimeout(config.dbIdleTimeout());
        hikariConfig.setMaxLifetime(config.dbMaxLifetime());
        hikariConfig.setConnectionTimeout(config.dbConnectionTimeout());
        if (dataSource != null) {
            dataSource.close();
        }
        dataSource = new HikariDataSource(hikariConfig);
        logger.info("Db initialized with updated config");
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void reloadConfig() {
        initializePool();
    }
}
