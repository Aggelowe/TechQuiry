package net.techquiry.app.common;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.sqlite.SQLiteConfig;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan({ "net.techquiry.app.database", "net.techquiry.app.service", "net.techquiry.app.mapper" })
public class TestAppConfiguration {

	@Primary
	@Bean(destroyMethod = "close")
	public DataSource getDataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl("jdbc:sqlite:file::memory:?cache=shared");
		hikariConfig.setMaximumPoolSize(1);
		hikariConfig.setAutoCommit(false);
		SQLiteConfig sqliteConfig = new SQLiteConfig();
		sqliteConfig.enforceForeignKeys(true);
		hikariConfig.setDataSourceProperties(sqliteConfig.toProperties());
		return new HikariDataSource(hikariConfig);
	}

}
