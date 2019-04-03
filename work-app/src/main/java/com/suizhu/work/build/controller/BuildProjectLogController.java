package com.suizhu.work.build.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.common.exception.ForbiddenException;
import com.suizhu.work.build.service.BuildEnginerService;
import com.suizhu.work.build.service.BuildProjectLogService;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildProjectLog;
import com.suizhu.work.entity.UserDoorway;
import com.suizhu.work.user.service.UserDoorwayService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 工程项目记录表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("buildProjectLog")
public class BuildProjectLogController {

	private final BuildProjectLogService buildProjectLogService;

	private final BuildProjectService buildProjectService;

	private final BuildEnginerService buildEnginerService;

	private final UserDoorwayService userDoorwayService;

	/**
	 * @dec 获取日志相关信息
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public R info(@PathVariable("id") String id) {
		Map<String, Object> data = new HashMap<>(4);
		data.put("nowDate", LocalDate.now().toString().replaceAll("-", "."));
		BuildProject buildProject = buildProjectService.getById(id);
		data.put("beginDate", buildProject.getBeginDate().toString().replaceAll("-", "."));
		data.put("endDate", buildProject.getEndDate().toString().replaceAll("-", "."));
		data.put("hasOption", true);
		if (BuildProject.STATUS_FLUSH == buildProject.getStatus()) {
			data.replace("hasOption", false);
		}

		return R.ok(data);
	}

	/**
	 * @dec 新增项目日志
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param buildProjectLog
	 * @param token
	 * @return
	 */
	@PostMapping
	public R save(@Valid BuildProjectLog buildProjectLog, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
		udqw.eq("type", UserDoorway.TYPE_DOORWAY).or(i -> i.eq("type", UserDoorway.TYPE_PROJECT))
				.and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(udqw);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		BuildProject buildProject = buildProjectService.getById(buildProjectLog.getBuildProjectId());
		if (BuildProject.STATUS_FLUSH != buildProject.getStatus()) {
			if (BuildProject.EARLY_OVERTIME_DOING == buildProject.getEarly()) {
				if (BuildProjectLog.STATUS_FLUSH == buildProjectLog.getStatus()) {
					buildProject.setStatus(BuildProject.STATUS_FLUSH);
					buildProject.setEarly(BuildProject.EARLY_OVERTIME_FLUSH);
					buildProject.setActualDate(LocalDate.now());
					buildProjectLog.setEarly(BuildProjectLog.EARLY_OVERTIME_FLUSH);
				}
				if (BuildProjectLog.STATUS_NORMAL == buildProjectLog.getStatus()) {
					buildProjectLog.setEarly(BuildProjectLog.EARLY_OVERTIME_DOING);
				}
			} else {
				LocalDate now = LocalDate.now();
				LocalDate endDate = buildProject.getEndDate();

				if (BuildProjectLog.STATUS_FLUSH == buildProjectLog.getStatus()) {
					buildProject.setStatus(BuildProject.STATUS_FLUSH);
					buildProject.setEarly(BuildProject.EARLY_NORMAL);
					buildProject.setActualDate(LocalDate.now());
					buildProjectLog.setEarly(BuildProjectLog.EARLY_NORMAL_FLUSH);
				}
				if (BuildProjectLog.STATUS_NORMAL == buildProjectLog.getStatus()) {
					if (now.isEqual(endDate)) {
						buildProject.setStatus(BuildProject.STATUS_FLUSH);
						buildProject.setEarly(BuildProject.EARLY_NORMAL);
						buildProject.setActualDate(LocalDate.now());
						buildProjectLog.setEarly(BuildProjectLog.EARLY_NORMAL);
					} else {
						buildProject.setStatus(BuildProject.STATUS_DOING);
						buildProject.setEarly(BuildProject.EARLY_NORMAL);
						buildProjectLog.setEarly(BuildProjectLog.EARLY_NORMAL_DOING);
					}
				}
				if (BuildProjectLog.STATUS_OVERTIME == buildProjectLog.getStatus()) {
					if (buildProjectLog.getStatus() == null) {
						throw new BadRequestException("请选择完成情况！");
					}

					buildProjectLog.setEarly(BuildProjectLog.EARLY_WARNING);
					buildProject.setEarly(BuildProject.EARLY_WARNING);
					buildProject.setStatus(BuildProject.STATUS_DOING);

					Integer overdueDays = buildProjectLog.getOverdueDays();
					if (overdueDays == null) {
						throw new BadRequestException("请输入预计超时时间！");
					}
					if (now.isEqual(endDate) || now.isAfter(endDate)) {
						overdueDays = overdueDays + buildProject.getOverdueDays();
						buildProject.setOverdueDays(overdueDays);
					}
				}
			}
		} else {
			buildProjectLog.setEarly(buildProject.getEarly());
		}

		buildProjectLog.setUserId(json.getStr("id"));
		String buildEnginerId = buildProject.getBuildEnginerId();
		String doorwayId = buildEnginerService.getOne("id", SqlEmnus.EQ, buildEnginerId, "id", "doorway_id")
				.getDoorwayId();
		boolean save = buildProjectLogService.add(buildProjectLog, doorwayId);
		boolean update = buildProjectService.updateById(buildProject);
		if (save && update) {
			int overdueDays = buildProject.getOverdueDays() != null ? buildProject.getOverdueDays() : 0;
			return R.ok(overdueDays);
		}

		return R.error();
	}

	/**
	 * @dec 删除项目日志
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@DeleteMapping("/{id}")
	public R del(@PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject jo = CommonUtil.decodeToken(token);
		BuildProjectLog buildProjectLog = buildProjectLogService.getById(id);
		if (!StrUtil.equals(buildProjectLog.getUserId(), jo.getStr("id"))) {
			throw new ForbiddenException("只能删除属于自己的日志！");
		}

		if (!buildProjectLog.getCreateTime().toLocalDate().isEqual(LocalDate.now())) {
			throw new ForbiddenException("只能删除当天的日志！");
		}

		return buildProjectLogService.del(id);
	}

	/**
	 * @dec 获取日志预览状态
	 * @date Apr 1, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param buildProjectId
	 * @param status
	 * @return
	 */
	@GetMapping("/early")
	public R early(String buildProjectId, Integer status) {
		if (StrUtil.isBlank(buildProjectId) || status == null) {
			throw new BadRequestException();
		}

		Map<String, Object> data = new HashMap<>(3);
		LocalDate now = LocalDate.now();
		int early = BuildProjectLog.EARLY_NORMAL_DOING;
		BuildProject buildProject = buildProjectService.getById(buildProjectId);
		if (buildProject.getStatus() != BuildProject.STATUS_FLUSH) {
			if (buildProject.getEarly() == BuildProject.EARLY_OVERTIME_DOING) {
				if (status == BuildProjectLog.STATUS_FLUSH) {
					early = BuildProjectLog.EARLY_OVERTIME_FLUSH;
				} else {
					early = BuildProjectLog.EARLY_OVERTIME_DOING;
				}
			} else {
				if (status == BuildProjectLog.STATUS_FLUSH) {
					early = BuildProjectLog.EARLY_NORMAL_FLUSH;
					if (now.isEqual(buildProject.getEndDate())) {
						early = BuildProjectLog.EARLY_NORMAL;
					}
				}
				if (status == BuildProjectLog.STATUS_NORMAL) {
					if (now.isEqual(buildProject.getEndDate())) {
						early = BuildProjectLog.EARLY_NORMAL;
					} else {
						early = BuildProjectLog.EARLY_NORMAL_DOING;
					}
				}
				if (status == BuildProjectLog.STATUS_OVERTIME) {
					early = BuildProjectLog.EARLY_WARNING;
				}
			}
		}

		data.put("early", early);
		long planDays = ChronoUnit.DAYS.between(buildProject.getBeginDate(), buildProject.getEndDate()) + 1;
		data.put("planDays", planDays);

		long actualDay = 0;
		if (buildProject.getBeginDate().isBefore(now) || buildProject.getBeginDate().isEqual(now)) {
			actualDay = ChronoUnit.DAYS.between(buildProject.getBeginDate(), now) + 1;
		}
		data.put("actualDay", actualDay);

		return R.ok(data);
	}

}
