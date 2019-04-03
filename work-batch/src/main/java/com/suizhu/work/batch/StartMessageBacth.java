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

import com.suizhu.work.batch.process.StartMessageProcess;
import com.suizhu.work.batch.read.StartMessageRead;
import com.suizhu.work.batch.write.StartMessageWrite;
import com.suizhu.work.entity.UserMessage;

import lombok.AllArgsConstructor;

@SpringBootConfiguration
@AllArgsConstructor
public class StartMessageBacth {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job startMessageJob(StartMessageRead smr, StartMessageProcess smp, StartMessageWrite smw) {
		return jobBuilderFactory.get("startMessageJob").incrementer(new RunIdIncrementer())
				.start(startMessageStep(smr, smp, smw)).build();
	}

	@Bean
	public Step startMessageStep(StartMessageRead smr, StartMessageProcess smp, StartMessageWrite smw) {
		return stepBuilderFactory.get("startMessageStep").<Map<String, Object>, List<UserMessage>>chunk(1).reader(smr)
				.processor(smp).writer(smw).build();
	}

}
