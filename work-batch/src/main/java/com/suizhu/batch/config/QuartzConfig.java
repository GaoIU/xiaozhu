package com.suizhu.batch.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Quartz配置
 * 
 * @author gaochao
 * @date Feb 24, 2019
 */
@SpringBootConfiguration
public class QuartzConfig {

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) throws IOException {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setOverwriteExistingJobs(true);
		factory.setAutoStartup(true);
		factory.setWaitForJobsToCompleteOnShutdown(true);
		factory.setDataSource(dataSource);
		return factory;
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
		return jobRegistryBeanPostProcessor;
	}

}
