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

import com.suizhu.work.batch.process.UnknownProcess;
import com.suizhu.work.batch.read.UnknownRead;
import com.suizhu.work.batch.write.UnknownWrite;
import com.suizhu.work.entity.UserMessage;

import lombok.AllArgsConstructor;

@SpringBootConfiguration
@AllArgsConstructor
public class UnknownBatch {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job unknownJob(UnknownRead ur, UnknownProcess up, UnknownWrite uw) {
		return jobBuilderFactory.get("unknownJob").incrementer(new RunIdIncrementer()).start(unknownStep(ur, up, uw))
				.build();
	}

	@Bean
	public Step unknownStep(UnknownRead ur, UnknownProcess up, UnknownWrite uw) {
		return stepBuilderFactory.get("unknownStep").<Map<String, Object>, List<UserMessage>>chunk(1).reader(ur)
				.processor(up).writer(uw).build();
	}

}
