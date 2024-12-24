package com.aggelowe.techquiry.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.sqlite.SQLiteConfig;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class TestSQLiteHikariConfig {

    @Primary
    @Bean(name = "dataSource", destroyMethod = "close")
    public static DataSource getDataSource() {

        log.info("Creating Datasource...");
        HikariConfig config = new HikariConfig();

        SQLiteConfig sqLiteConfig = new SQLiteConfig();
        sqLiteConfig.enforceForeignKeys(true);
        config.setDataSourceProperties(sqLiteConfig.toProperties());
        
        // Set SQLite JDBC URL (Replace with your SQLite database path)
        config.setJdbcUrl("jdbc:sqlite:file:memdb1?mode=memory&cache=shared");

        // SQLite doesn't require username and password, but they must be set for HikariCP
        config.setUsername("");
        config.setPassword("");

        // HikariCP settings
        config.setMaximumPoolSize(10); // Set max pool size
        config.setIdleTimeout(30000); // 30 seconds idle timeout
        config.setConnectionTimeout(30000); // 30 seconds connection timeout
        config.setMinimumIdle(2); // Minimum number of idle connections

        // Optional: SQLite-specific optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setAutoCommit(false);
 

        log.info("Datasource created successfully.");
        return new HikariDataSource(config);
    }

}