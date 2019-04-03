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

import com.suizhu.work.batch.process.CleanFileProcess;
import com.suizhu.work.batch.read.CleanFileRead;
import com.suizhu.work.batch.write.CleanFileWrite;

import lombok.AllArgsConstructor;

/**
 * 要清除的图片文件数据监控
 * 
 * @author gaochao
 * @date Mar 6, 2019
 */
@SpringBootConfiguration
@AllArgsConstructor
public class CleanFileBatch {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job cleanFileJob(CleanFileRead fr, CleanFileProcess fp, CleanFileWrite fw) {
		return jobBuilderFactory.get("cleanFileJob").incrementer(new RunIdIncrementer())
				.start(cleanFileStep(fr, fp, fw)).build();
	}

	@Bean
	public Step cleanFileStep(CleanFileRead fr, CleanFileProcess fp, CleanFileWrite fw) {
		return stepBuilderFactory.get("cleanFileStep").<Map<String, Object>, Map<String, List<String>>>chunk(1)
				.reader(fr).processor(fp).writer(fw).build();
	}

}
