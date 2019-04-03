package com.suizhu.work.batch.read;

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
import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.Doorway;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DoorwayRead implements ItemReader<Map<String, List<String>>> {

	private final DoorwayService doorwayService;

	private final BuildEnginerService buildEnginerService;

	@Override
	public Map<String, List<String>> read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		List<Doorway> list = doorwayService.list("status", SqlEmnus.EQ, Doorway.STATUS_NOT_START, "id");
		if (CollUtil.isNotEmpty(list)) {
			Map<String, List<String>> data = new HashMap<>(2);
			List<String> doorwayIds = list.stream().map(Doorway::getId).collect(Collectors.toList());
			QueryWrapper<BuildEnginer> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("status", BuildEnginer.STATUS_DOING);
			queryWrapper.and(i -> i.in("doorway_id", doorwayIds));
			queryWrapper.groupBy("doorway_id").select("doorway_id");
			List<BuildEnginer> bes = buildEnginerService.list(queryWrapper);
			if (CollUtil.isNotEmpty(bes)) {
				List<String> doingIds = bes.stream().map(BuildEnginer::getDoorwayId).collect(Collectors.toList());
				data.put("doingIds", doingIds);
			}

			queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("status", BuildEnginer.STATUS_FLUSH);
			queryWrapper.and(i -> i.in("doorway_id", doorwayIds));
			queryWrapper.groupBy("doorway_id").select("doorway_id");
			List<BuildEnginer> belush = buildEnginerService.list(queryWrapper);
			if (CollUtil.isNotEmpty(belush)) {
				List<String> flushIds = belush.stream().map(BuildEnginer::getDoorwayId).collect(Collectors.toList());
				data.put("flushIds", flushIds);
			}

			if (CollUtil.isNotEmpty(data)) {
				return data;
			}

		}

		return null;
	}

}
