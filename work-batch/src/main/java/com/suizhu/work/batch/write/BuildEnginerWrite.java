package com.suizhu.work.batch.write;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.suizhu.work.build.service.BuildEnginerService;
import com.suizhu.work.entity.BuildEnginer;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BuildEnginerWrite implements ItemWriter<List<BuildEnginer>> {

	private final BuildEnginerService buildEnginerService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void write(List<? extends List<BuildEnginer>> items) throws Exception {
		if (CollUtil.isNotEmpty(items)) {
			items.forEach(i -> {
				buildEnginerService.updateBatchById(i);
			});
		}
	}

}
