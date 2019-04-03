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

import com.suizhu.work.batch.process.BuildEnginerProcess;
import com.suizhu.work.batch.read.BuildEnginerRead;
import com.suizhu.work.batch.write.BuildEnginerWrite;
import com.suizhu.work.entity.BuildEnginer;

import lombok.AllArgsConstructor;

/**
 * 筹建工程开工监控
 * 
 * @author gaochao
 * @date Mar 7, 2019
 */
@SpringBootConfiguration
@AllArgsConstructor
public class BuildEnginerBatch {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job buildEnginerJob(BuildEnginerRead ber, BuildEnginerProcess bep, BuildEnginerWrite bew) {
		return jobBuilderFactory.get("buildEnginerJob").incrementer(new RunIdIncrementer())
				.start(buildEnginerStep(ber, bep, bew)).build();
	}

	@Bean
	public Step buildEnginerStep(BuildEnginerRead ber, BuildEnginerProcess bep, BuildEnginerWrite bew) {
		return stepBuilderFactory.get("buildEnginerStep").<Map<String, List<BuildEnginer>>, List<BuildEnginer>>chunk(1)
				.reader(ber).processor(bep).writer(bew).build();
	}

}
