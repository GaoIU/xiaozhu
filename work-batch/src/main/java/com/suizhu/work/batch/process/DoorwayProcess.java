package com.suizhu.work.batch.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.Doorway;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DoorwayProcess implements ItemProcessor<Map<String, List<String>>, List<Doorway>> {

	private final DoorwayService doorwayService;

	@Override
	public List<Doorway> process(Map<String, List<String>> item) throws Exception {
		if (CollUtil.isNotEmpty(item)) {
			List<Doorway> list = new ArrayList<>();
			List<String> doingIds = item.get("doingIds");
			if (CollUtil.isNotEmpty(doingIds)) {
				List<Doorway> doings = doorwayService.list("id", SqlEmnus.IN, doingIds, "id", "status");
				doings.forEach(d -> {
					d.setStatus(Doorway.STATUS_DOING);
				});
				list.addAll(doings);
			}

			List<String> flushIds = item.get("flushIds");
			if (CollUtil.isNotEmpty(flushIds)) {
				List<Doorway> flushs = doorwayService.list("id", SqlEmnus.IN, flushIds, "id", "status");
				flushs.forEach(f -> {
					f.setStatus(Doorway.STATUS_FLUSH);
				});
				list.addAll(flushs);
			}

			if (CollUtil.isNotEmpty(list)) {
				return list;
			}
		}

		return null;
	}

}
