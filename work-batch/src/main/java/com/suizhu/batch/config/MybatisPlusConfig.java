package com.suizhu.batch.config;

import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.suizhu.common.core.MapWrapperFactory;

@SpringBootConfiguration
@MapperScan("com.suizhu.**.mapper")
public class MybatisPlusConfig {

	@Bean
	public PaginationInterceptor paginationInterceptor() {
		return new PaginationInterceptor();
	}

	public ConfigurationCustomizer configurationCustomizer() {
		return new ConfigurationCustomizer() {

			@Override
			public void customize(Configuration configuration) {
				configuration.setObjectWrapperFactory(new MapWrapperFactory());
			}
		};
	}

}
