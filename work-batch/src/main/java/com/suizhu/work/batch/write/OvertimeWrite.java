package com.suizhu.work.batch.write;

import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.entity.BuildProject;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

/**
 * 筹建工程项目超时数据写入
 * 
 * @author gaochao
 * @date Mar 6, 2019
 */
@Component
@AllArgsConstructor
public class OvertimeWrite implements ItemWriter<Map<String, List<BuildProject>>> {

	private final BuildProjectService buildProjectService;

	@Override
	public void write(List<? extends Map<String, List<BuildProject>>> items) throws Exception {
		if (CollUtil.isNotEmpty(items)) {
			items.forEach(m -> {
				List<BuildProject> doingList = m.get("doingList");
				if (CollUtil.isNotEmpty(doingList)) {
					buildProjectService.updateBatchById(doingList);
				}

				List<BuildProject> earlyList = m.get("earlyList");
				if (CollUtil.isNotEmpty(earlyList)) {
					buildProjectService.updateBatchById(earlyList);
				}

				List<BuildProject> overtimeList = m.get("overtimeList");
				if (CollUtil.isNotEmpty(overtimeList)) {
					buildProjectService.updateBatchById(overtimeList);
				}
			});
		}
	}

}
