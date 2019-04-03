package com.suizhu.work.batch.read;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.work.build.service.BuildEnginerService;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildProject;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

/**
 * 筹建工程已经开始筹建的数据
 * 
 * @author gaochao
 * @date Mar 7, 2019
 */
@Component
@AllArgsConstructor
public class BuildEnginerRead implements ItemReader<Map<String, List<BuildEnginer>>> {

	private final BuildEnginerService buildEnginerService;

	private final BuildProjectService buildProjectService;

	@Override
	public Map<String, List<BuildEnginer>> read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Map<String, List<BuildEnginer>> data = new HashMap<>(2);

		QueryWrapper<BuildEnginer> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("status", BuildEnginer.STATUS_NOT_START);
		queryWrapper.and(i -> i.ge("prepare_date", LocalDate.now().toString()));
		queryWrapper.select("id", "status");
		List<BuildEnginer> list = buildEnginerService.list(queryWrapper);
		if (CollUtil.isNotEmpty(list)) {
			data.put("doing", list);
		}

		List<String> beIdList = new ArrayList<>();
		List<BuildEnginer> bes = buildEnginerService.list("status", SqlEmnus.NE, BuildEnginer.STATUS_FLUSH, "id");
		if (CollUtil.isNotEmpty(bes)) {
			List<String> beIds = bes.stream().map(BuildEnginer::getId).collect(Collectors.toList());
			beIds.forEach(id -> {
				boolean allMatch = buildProjectService.list("build_enginer_id", SqlEmnus.EQ, id).stream()
						.allMatch(bp -> BuildProject.STATUS_FLUSH == bp.getStatus());
				if (allMatch) {
					beIdList.add(id);
				}
			});
		}

		if (CollUtil.isNotEmpty(beIdList)) {
			List<BuildEnginer> flush = buildEnginerService.list("id", SqlEmnus.IN, beIdList, "id", "status");
			data.put("flush", flush);
		}

		if (CollUtil.isNotEmpty(data)) {
			return data;
		}

		return null;
	}

}
