package org.pj.Config.Db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DbConfig {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:xe");
        config.setUsername("FEE_DEV");
        config.setPassword("FEE_DEV");
        config.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        config.setMaximumPoolSize(20); // So luong ket noi toi da
        config.setMinimumIdle(5); // So luong ket noi toi thieu
        config.setIdleTimeout(300000); // Thoi gian mot ket noi duoc giu lai khi khong su dung
        config.setMaxLifetime(1800000); // Thoi gian toi da mot ket noi duoc su dung
        config.setConnectionTimeout(30000); // Thoi gian toi da de lay duoc ket noi tu pool
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
