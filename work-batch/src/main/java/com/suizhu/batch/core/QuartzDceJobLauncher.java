package com.suizhu.batch.core;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.suizhu.common.util.SpringContextHolder;

@DisallowConcurrentExecution
public class QuartzDceJobLauncher extends QuartzJobBean {

	private final JobLauncher jobLauncher = SpringContextHolder.getBean("jobLauncher");

	private final JobRegistry jobRegistry = SpringContextHolder.getBean("jobRegistry");

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			Job job = jobRegistry.getJob(context.getJobDetail().getKey().getName());
			JobParameters jobParameters = new JobParametersBuilder().addLong("taskId", System.currentTimeMillis())
					.toJobParameters();
			jobLauncher.run(job, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
