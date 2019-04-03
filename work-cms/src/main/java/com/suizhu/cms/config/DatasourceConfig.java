package com.suizhu.cms.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 数据库连接池配置
 * 
 * @author gaochao
 * @date Feb 20, 2019
 */
@SpringBootConfiguration
public class DatasourceConfig {

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Bean
	public DataSource primaryDataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(url);
		hikariConfig.setUsername(username);
		hikariConfig.setPassword(password);
		// 是否自定义配置，为true时下面两个参数才生效
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		// 连接池大小默认25，官方推荐250-500
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "300");
		// 单条语句最大长度默认256，官方推荐2048
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "1024");
		// 新版本MySQL支持服务器端准备，开启能够得到显著性能提升
		hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
		hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
		hikariConfig.addDataSourceProperty("useLocalTransactionState", "true");
		hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
		hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
		hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
		hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
		hikariConfig.addDataSourceProperty("maintainTimeStats", "false");

		return new HikariDataSource(hikariConfig);
	}

}
