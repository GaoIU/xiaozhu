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

import com.suizhu.work.batch.process.OverMessageProcess;
import com.suizhu.work.batch.read.OverMessageRead;
import com.suizhu.work.batch.write.OverMessageWrite;
import com.suizhu.work.entity.UserMessage;

import lombok.AllArgsConstructor;

@SpringBootConfiguration
@AllArgsConstructor
public class OverMessageBatch {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job overMessageJob(OverMessageRead omr, OverMessageProcess omp, OverMessageWrite omw) {
		return jobBuilderFactory.get("overMessageJob").incrementer(new RunIdIncrementer())
				.start(overMessageStep(omr, omp, omw)).build();
	}

	@Bean
	public Step overMessageStep(OverMessageRead omr, OverMessageProcess omp, OverMessageWrite omw) {
		return stepBuilderFactory.get("overMessageStep").<Map<String, Object>, List<UserMessage>>chunk(1).reader(omr)
				.processor(omp).writer(omw).build();
	}

}
