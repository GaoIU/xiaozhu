package com.suizhu.work.batch.read;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.work.build.service.BuildEnginerService;
import com.suizhu.work.build.service.BuildProjectLogService;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.build.service.BuildTipService;
import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildProjectLog;
import com.suizhu.work.entity.BuildTip;
import com.suizhu.work.entity.Doorway;
import com.suizhu.work.entity.UserMessage;
import com.suizhu.work.user.service.UserMessageService;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UnknownRead implements ItemReader<Map<String, Object>> {

	private final BuildProjectService buildProjectService;

	private final BuildProjectLogService buildProjectLogService;

	private final BuildTipService buildTipService;

	private final UserMessageService userMessageService;

	private final DoorwayService doorwayService;

	private final BuildEnginerService buildEnginerService;

	@Override
	public Map<String, Object> read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		String date = LocalDate.now().toString();
		List<BuildProject> list = buildProjectService.list("begin_date", SqlEmnus.EQ, date, "id", "build_enginer_id",
				"name");
		if (CollUtil.isNotEmpty(list)) {// 今天所有的工序
			List<BuildProject> result = new ArrayList<>();

			list.forEach(bp -> {
				QueryWrapper<BuildProjectLog> queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("build_project_id", bp.getId()).and(i -> i.eq("create_time", date));
				int count = buildProjectLogService.count(queryWrapper);
				if (count <= 0) {
					result.add(bp);
				}
			});

			if (CollUtil.isNotEmpty(result)) {// 没有确认任务的工序
				Set<String> beIds = result.stream().map(BuildProject::getBuildEnginerId).collect(Collectors.toSet());
				if (CollUtil.isNotEmpty(beIds)) {// 开启提示的工程
					QueryWrapper<BuildTip> queryWrapper = new QueryWrapper<>();
					queryWrapper.eq("remind", BuildTip.REMIND_OPEN).and(i -> i.in("build_enginer_id", beIds));
					queryWrapper.select("user_id", "build_enginer_id");
					List<BuildTip> bts = buildTipService.list(queryWrapper);

					Set<String> beSet = bts.stream().map(BuildTip::getBuildEnginerId).collect(Collectors.toSet());
					List<String> doorwayIds = buildEnginerService.list("id", SqlEmnus.IN, beSet, "doorway_id").stream()
							.map(BuildEnginer::getDoorwayId).collect(Collectors.toList());
					List<Doorway> doorways = doorwayService.list("id", SqlEmnus.IN, doorwayIds, "id", "name");

					QueryWrapper<UserMessage> umqw = new QueryWrapper<>();
					umqw.eq("type", UserMessage.TYPE_NOT_FLUSH).and(i -> i.in("doorway_id", doorwayIds))
							.and(i -> i.ge("create_time", date + " 00:00:00"));
					int count = userMessageService.count(umqw);

					if (count <= 0) {// 未发送推送
						if (CollUtil.isNotEmpty(beSet)) {
							QueryWrapper<BuildProject> wrapper = new QueryWrapper<>();
							wrapper.eq("begin_date", date).and(i -> i.in("build_enginer_id", beSet));
							wrapper.select("id", "build_enginer_id", "name");
							list = buildProjectService.list(wrapper);
						} else {
							list = new ArrayList<>(0);
						}

						Map<String, Object> data = new HashMap<>(3);
						data.put("doorways", doorways);
						data.put("list", list);
						data.put("bts", bts);
						return data;
					}
				}
			}
		}

		return null;
	}

}
