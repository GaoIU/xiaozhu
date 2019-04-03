package com.suizhu.work.batch.write;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.Doorway;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DoorwayWrite implements ItemWriter<List<Doorway>> {

	private final DoorwayService doorwayService;

	@Override
	public void write(List<? extends List<Doorway>> items) throws Exception {
		if (CollUtil.isNotEmpty(items)) {
			items.forEach(i -> {
				doorwayService.updateBatchById(i);
			});
		}
	}

}
