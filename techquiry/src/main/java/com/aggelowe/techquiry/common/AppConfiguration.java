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
 * responsible for defining key application configuration beans.
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
	public DataSource getDataSource() {
		Path databasePath = Environment.SERVER_WORK_DIRECTORY.toPath().resolve(Constants.DATABASE_FILENAME);
		String databaseUrl = "jdbc:sqlite:" + databasePath;
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setAutoCommit(false);
		hikariConfig.setJdbcUrl(databaseUrl);
		hikariConfig.setConnectionTimeout(Environment.DATABASE_TIMEOUT);
		hikariConfig.setIdleTimeout(Environment.DATABASE_IDLE_TIMEOUT);
		hikariConfig.setMaxLifetime(Environment.DATABASE_LIFETIME);
		hikariConfig.setMaximumPoolSize(Environment.DATABASE_POOL_SIZE);
		hikariConfig.setPoolName(Constants.APPLICATION_NAME + "ConnectionPool");
		SQLiteConfig sqliteConfig = new SQLiteConfig();
		sqliteConfig.enforceForeignKeys(true);
		hikariConfig.setDataSourceProperties(sqliteConfig.toProperties());
		return new HikariDataSource(hikariConfig);
	}

}
