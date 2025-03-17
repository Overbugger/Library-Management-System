package org.library.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DbConnection {
    private static HikariDataSource dataSource;

    private static final String URL = "jdbc:postgresql://ep-crimson-sound-a9hi40hn-pooler.gwc.azure.neon.tech:5432/TestDB?sslmode=require";
    private static final String USER = "TestDB_owner";
    private static final String PASSWORD = "npg_g7zEXSmVv8bu";

    private DbConnection() {
        // private constructor to prevent instantiation
    }

    static {
        initializeDatasource();
    }

    private static void initializeDatasource() {
        HikariConfig config = new HikariConfig();

        // Use PostgreSQL for production
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);

        // Connection pool settings
        config.setMaximumPoolSize(10); // Maximum number of connections in the pool
        config.setMinimumIdle(2); // Minimum number of idle connections
        config.setIdleTimeout(30000); // Idle timeout (in milliseconds)
        config.setMaxLifetime(1800000); // Maximum lifetime of a connection (in milliseconds)

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
