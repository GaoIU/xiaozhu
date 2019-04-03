package com.suizhu.work.batch;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import com.suizhu.work.batch.process.EarlyMessageProcess;
import com.suizhu.work.batch.read.EarlyMessageRead;
import com.suizhu.work.batch.write.EarlyMessageWrite;
import com.suizhu.work.entity.UserMessage;

import lombok.AllArgsConstructor;

@SpringBootConfiguration
@AllArgsConstructor
public class EarlyMessageBatch {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job earlyMessageJob(EarlyMessageRead emr, EarlyMessageProcess emp, EarlyMessageWrite emw) {
		return jobBuilderFactory.get("earlyMessageJob").incrementer(new RunIdIncrementer())
				.start(earlyMessageStep(emr, emp, emw)).build();
	}

	@Bean
	public Step earlyMessageStep(EarlyMessageRead emr, EarlyMessageProcess emp, EarlyMessageWrite emw) {
		return stepBuilderFactory.get("earlyMessageStep").<Map<String, Object>, List<UserMessage>>chunk(1).reader(emr)
				.processor(emp).writer(emw).build();
	}

}
