package com.aggelowe.techquiry.common;

import java.nio.file.Path;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteConfig;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The {@link AppConfiguration} class is a Spring configuration class that is
 * responsible for defining key application configuration settings.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Configuration
@ComponentScan("com.aggelowe.techquiry")
public class AppConfiguration {

	/**
	 * This method configures and returns a new {@link DataSource} object for
	 * connecting to the application database.
	 * 
	 * @return The {@link DataSource} instance
	 */
	@Bean(destroyMethod = "close")
	public static DataSource getDataSource() {
		Path databasePath = Environment.getWorkDirectory().toPath().resolve(Constants.DATABASE_FILENAME);
		String databaseUrl = "jdbc:sqlite:" + databasePath;
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(databaseUrl);
		hikariConfig.setMaximumPoolSize(Environment.getConnectionPoolSize());
		hikariConfig.setIdleTimeout(30000);
		hikariConfig.setConnectionTimeout(30000);
		hikariConfig.setMinimumIdle(2);
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hikariConfig.setAutoCommit(false);
		SQLiteConfig sqliteConfig = new SQLiteConfig();
		sqliteConfig.enforceForeignKeys(true);
		hikariConfig.setDataSourceProperties(sqliteConfig.toProperties());
		return new HikariDataSource(hikariConfig);
	}

}
