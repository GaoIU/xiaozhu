package com.suizhu.work.batch.read;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.work.build.service.BuildProjectLogService;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildProjectLog;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

/**
 * 筹建工程项目超时数据读取
 * 
 * @author gaochao
 * @date Mar 6, 2019
 */
@Component
@AllArgsConstructor
public class OvertimeRead implements ItemReader<Map<String, List<BuildProject>>> {

	private final BuildProjectService buildProjectService;

	private final BuildProjectLogService buildProjectLogService;

	@Override
	public Map<String, List<BuildProject>> read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		Map<String, List<BuildProject>> data = new HashMap<>(3);
		String date = LocalDate.now().toString();

		QueryWrapper<BuildProject> doingQuery = new QueryWrapper<>();
		doingQuery.eq("status", BuildProject.STATUS_NOT_START).and(i -> i.le("begin_date", date));
		doingQuery.and(i -> i.ge("end_date", date));
		List<BuildProject> doingList = buildProjectService.list(doingQuery);
		if (CollUtil.isNotEmpty(doingList)) {
			data.put("doingList", doingList);
		}

		LocalDate yestoday = LocalDate.now().plusDays(-1);
		QueryWrapper<BuildProject> earlyQuery = new QueryWrapper<>();
		earlyQuery.eq("status", BuildProject.STATUS_DOING).and(i -> i.lt("begin_date", date));
		earlyQuery.and(i -> i.ge("end_date", date)).and(i -> i.eq("early", BuildProject.EARLY_NORMAL));
		List<BuildProject> earlyList = buildProjectService.list(earlyQuery);
		List<BuildProject> earlys = new ArrayList<>();
		earlyList.forEach(bp -> {
			QueryWrapper<BuildProjectLog> wrapper = new QueryWrapper<>();
			wrapper.eq("build_project_id", bp.getId());
			wrapper.and(i -> i.ge("create_time", yestoday.toString() + " 00:00:00"));
			wrapper.and(i -> i.lt("create_time", date + " 00:00:00"));
			int count = buildProjectLogService.count(wrapper);

			if (count <= 0) {
				earlys.add(bp);
			}
		});
		if (CollUtil.isNotEmpty(earlys)) {
			data.put("earlyList", earlys);
		}

		QueryWrapper<BuildProject> queryWrapper = new QueryWrapper<>();
		queryWrapper.ne("status", BuildProject.STATUS_FLUSH).and(i -> i.lt("end_date", date));
		queryWrapper.and(i -> i.ne("early", BuildProject.EARLY_OVERTIME_DOING));
		List<BuildProject> list = buildProjectService.list(queryWrapper);
		if (CollUtil.isNotEmpty(list)) {
			data.put("overtimeList", list);
		}

		if (CollUtil.isNotEmpty(data)) {
			return data;
		}

		return null;
	}

}
