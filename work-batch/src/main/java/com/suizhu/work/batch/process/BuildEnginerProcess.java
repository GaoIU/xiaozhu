package com.suizhu.work.batch.process;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.suizhu.work.entity.BuildEnginer;

import cn.hutool.core.collection.CollUtil;

@Component
public class BuildEnginerProcess implements ItemProcessor<Map<String, List<BuildEnginer>>, List<BuildEnginer>> {

	@Override
	public List<BuildEnginer> process(Map<String, List<BuildEnginer>> item) throws Exception {
		if (CollUtil.isNotEmpty(item)) {
			List<BuildEnginer> doing = item.get("doing");
			List<BuildEnginer> flush = item.get("flush");

			List<BuildEnginer> list = new ArrayList<>();

			if (CollUtil.isNotEmpty(doing)) {
				doing.forEach(b -> {
					b.setStatus(BuildEnginer.STATUS_DOING);
					list.add(b);
				});
			}

			if (CollUtil.isNotEmpty(flush)) {
				LocalDate date = LocalDate.now().plusDays(-1);
				flush.forEach(b -> {
					b.setStatus(BuildEnginer.STATUS_FLUSH);
					b.setActualDate(date);
					list.add(b);
				});
			}

			if (CollUtil.isNotEmpty(list)) {
				return list;
			}
		}

		return null;
	}

}
