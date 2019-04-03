package com.suizhu.work.batch.process;

import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.suizhu.work.entity.BuildProject;

import cn.hutool.core.collection.CollUtil;

/**
 * 筹建工程项目超时数据处理
 * 
 * @author gaochao
 * @date Mar 6, 2019
 */
@Component
public class OvertimeProcess
		implements ItemProcessor<Map<String, List<BuildProject>>, Map<String, List<BuildProject>>> {

	@Override
	public Map<String, List<BuildProject>> process(Map<String, List<BuildProject>> item) throws Exception {
		if (CollUtil.isNotEmpty(item)) {
			List<BuildProject> doingList = item.get("doingList");
			if (CollUtil.isNotEmpty(doingList)) {
				doingList.forEach(dl -> {
					dl.setStatus(BuildProject.STATUS_DOING);
					dl.setEarly(BuildProject.EARLY_NORMAL);
				});

				item.replace("doingList", doingList);
			}

			List<BuildProject> earlyList = item.get("earlyList");
			if (CollUtil.isNotEmpty(earlyList)) {
				earlyList.forEach(el -> {
					el.setEarly(BuildProject.EARLY_WARNING);
				});

				item.replace("earlyList", earlyList);
			}

			List<BuildProject> overtimeList = item.get("overtimeList");
			if (CollUtil.isNotEmpty(overtimeList)) {
				overtimeList.forEach(ol -> {
					ol.setStatus(BuildProject.STATUS_DOING);
					ol.setEarly(BuildProject.EARLY_OVERTIME_DOING);
				});

				item.replace("overtimeList", overtimeList);
			}

			return item;
		}

		return null;
	}

}
