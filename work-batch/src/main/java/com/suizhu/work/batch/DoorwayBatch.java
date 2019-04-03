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

import com.suizhu.work.batch.process.DoorwayProcess;
import com.suizhu.work.batch.read.DoorwayRead;
import com.suizhu.work.batch.write.DoorwayWrite;
import com.suizhu.work.entity.Doorway;

import lombok.AllArgsConstructor;

/**
 * 门店状态监控
 * 
 * @author gaochao
 * @date Mar 7, 2019
 */
@SpringBootConfiguration
@AllArgsConstructor
public class DoorwayBatch {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job doorwayJob(DoorwayRead dr, DoorwayProcess dp, DoorwayWrite dw) {
		return jobBuilderFactory.get("doorwayJob").incrementer(new RunIdIncrementer()).start(doorwayStep(dr, dp, dw))
				.build();
	}

	@Bean
	public Step doorwayStep(DoorwayRead dr, DoorwayProcess dp, DoorwayWrite dw) {
		return stepBuilderFactory.get("doorwayStep").<Map<String, List<String>>, List<Doorway>>chunk(1).reader(dr)
				.processor(dp).writer(dw).build();
	}

}
