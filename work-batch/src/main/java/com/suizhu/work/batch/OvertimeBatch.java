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

import com.suizhu.work.batch.process.OvertimeProcess;
import com.suizhu.work.batch.read.OvertimeRead;
import com.suizhu.work.batch.write.OvertimeWrite;
import com.suizhu.work.entity.BuildProject;

import lombok.AllArgsConstructor;

/**
 * 筹建工程项目超时监控
 * 
 * @author gaochao
 * @date Mar 6, 2019
 */
@SpringBootConfiguration
@AllArgsConstructor
public class OvertimeBatch {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job overtimeJob(OvertimeRead or, OvertimeProcess op, OvertimeWrite ow) {
		return jobBuilderFactory.get("overtimeJob").incrementer(new RunIdIncrementer()).start(overtimeStep(or, op, ow))
				.build();
	}

	@Bean
	public Step overtimeStep(OvertimeRead or, OvertimeProcess op, OvertimeWrite ow) {
		return stepBuilderFactory.get("overtimeStep")
				.<Map<String, List<BuildProject>>, Map<String, List<BuildProject>>>chunk(1).reader(or).processor(op)
				.writer(ow).build();
	}

}
