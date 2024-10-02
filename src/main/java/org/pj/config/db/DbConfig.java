package org.pj.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.aeonbits.owner.ConfigFactory;
import org.pj.config.AppConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class DbConfig {
    private static final HikariDataSource dataSource;

    static {
        AppConfig config = ConfigFactory.create(AppConfig.class);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.dbUrl());
        hikariConfig.setUsername(config.dbUsername());
        hikariConfig.setPassword(config.dbPassword());
        hikariConfig.setDriverClassName(config.dbDriverClassName());
        hikariConfig.setMaximumPoolSize(config.dbMaxPoolSize()); // So luong ket noi toi da
        hikariConfig.setMinimumIdle(config.dbMinIdle()); // So luong ket noi toi thieu
        hikariConfig.setIdleTimeout(config.dbIdleTimeout()); // Thoi gian mot ket noi duoc giu lai khi khong su dung
        hikariConfig.setMaxLifetime(config.dbMaxLifetime()); // Thoi gian toi da mot ket noi duoc su dung
        hikariConfig.setConnectionTimeout(config.dbConnectionTimeout()); // Thoi gian toi da de lay duoc ket noi tu pool
        dataSource = new HikariDataSource(hikariConfig);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
