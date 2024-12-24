package com.aggelowe.techquiry.config;

import static com.aggelowe.techquiry.common.Constants.*;

import java.nio.file.Path;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.sqlite.SQLiteConfig;

import com.aggelowe.techquiry.common.Environment;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

//@Configuration
public class SQLiteHikariConfig {

//    @Primary
//    @Bean(name = "dataSource", destroyMethod = "close")
    public static DataSource getDataSource() {

        Path databasePath = Environment.getWorkDirectory().toPath().resolve(DATABASE_FILENAME);

        HikariConfig config = new HikariConfig();

        // Set SQLite JDBC URL (Replace with your SQLite database path)
        config.setJdbcUrl("jdbc:sqlite:" + databasePath);

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
        
        SQLiteConfig sqLiteConfig = new SQLiteConfig();
        sqLiteConfig.enforceForeignKeys(true);
        config.setDataSourceProperties(sqLiteConfig.toProperties());

        return new HikariDataSource(config);
    }

}