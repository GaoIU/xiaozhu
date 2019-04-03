package com.suizhu.work.build.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.suizhu.work.doorway.service.DoorwayCategoryService;
import com.suizhu.work.doorway.service.DoorwayModelService;
import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildProjectLog;
import com.suizhu.work.entity.Doorway;
import com.suizhu.work.entity.DoorwayCategory;
import com.suizhu.work.entity.DoorwayModel;
import com.suizhu.work.entity.User;
import com.suizhu.work.entity.UserDoorway;
import com.suizhu.work.entity.UserOrg;
import com.suizhu.work.model.service.ModelBuildService;
import com.suizhu.work.user.service.UserDoorwayService;
import com.suizhu.work.user.service.UserOrgService;
import com.suizhu.work.user.service.UserService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 筹建工程表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("buildEnginer")
public class BuildEnginerController {

	private final BuildEnginerService buildEnginerService;

	private final BuildProjectService buildProjectService;

	private final BuildProjectLogService buildProjectLogService;

	private final UserOrgService userOrgService;

	private final DoorwayService doorwayService;

	private final UserService userService;

	private final UserDoorwayService userDoorwayService;

	private final ModelBuildService modelBuildService;

	private final DoorwayModelService doorwayModelService;

	private final DoorwayCategoryService doorwayCategoryService;

	/**
	 * @dec 新增工程信息
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param buildEnginer
	 * @param token
	 * @return
	 */
	@PostMapping
	public R save(@Valid BuildEnginer buildEnginer, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		validate(buildEnginer);

		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		if (userOrg == null) {
			throw new ForbiddenException();
		}

		Doorway doorway = doorwayService.getOne("id", SqlEmnus.EQ, buildEnginer.getDoorwayId(), "id", "status");
		LocalDate prepareDate = buildEnginer.getPrepareDate();
		LocalDate now = LocalDate.now();
		if (prepareDate.isBefore(now) || prepareDate.isEqual(now)) {
			buildEnginer.setStatus(BuildEnginer.STATUS_DOING);
			doorway.setStatus(Doorway.STATUS_DOING);
			doorwayService.updateById(doorway);
		} else {
			buildEnginer.setStatus(BuildEnginer.STATUS_NOT_START);
		}

		return buildEnginerService.addBuildEnginer(buildEnginer, json);
	}

	/**
	 * @dec 参数验证
	 * @date Mar 28, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param buildEnginer
	 */
	private void validate(BuildEnginer buildEnginer) {
		String[] seriesNames = buildEnginer.getSeriesName().split(",");
		String[] seriesMobiles = buildEnginer.getSeriesMobile().split(",");
		if (seriesNames.length != seriesMobiles.length) {
			throw new BadRequestException("工程经理输入格式有误！");
		}
		boolean ab = Stream.of(seriesNames).anyMatch(sn -> StrUtil.isBlank(sn));
		if (ab) {
			throw new BadRequestException("工程经理名称不能为空！");
		}
		boolean n = Stream.of(seriesNames).anyMatch(sn -> sn.length() > 12);
		if (n) {
			throw new BadRequestException("工程经理名称长度不能超过12位！");
		}
		boolean m = Stream.of(seriesMobiles).anyMatch(sm -> !CommonUtil.checkMobile(sm));
		if (m) {
			throw new BadRequestException("工程经理手机号码不合法！");
		}

		String delSeriesMobile = buildEnginer.getDelSeriesMobile();
		if (StrUtil.isNotBlank(delSeriesMobile)) {
			String[] delSeriesMobiles = delSeriesMobile.split(",");
			int delSmLength = delSeriesMobiles.length;
			for (String sm : delSeriesMobiles) {
				for (int i = 0; i < seriesMobiles.length; i++) {
					if (StrUtil.equals(sm, seriesMobiles[i])) {
						String serMob = seriesMobiles[i];
						if (delSmLength > 1) {
							serMob = "," + serMob;
						}
						delSeriesMobile = delSeriesMobile.replaceAll(serMob, "");
					}
				}
			}
		}
		String delProjectMobile = buildEnginer.getDelProjectMobile();
		if (StrUtil.isNotBlank(delProjectMobile)) {
			String[] delProjectMobiles = delProjectMobile.split(",");
			int delPmLength = delProjectMobiles.length;
			for (String pm : delProjectMobiles) {
				for (int i = 0; i < seriesMobiles.length; i++) {
					if (StrUtil.equals(pm, seriesMobiles[i])) {
						String perMob = seriesMobiles[i];
						if (delPmLength > 1) {
							perMob = "," + perMob;
						}
						delProjectMobile = delProjectMobile.replaceAll(perMob, "");
					}
				}
			}
		}

		String[] ownerNames = buildEnginer.getOwnerName().split(",");
		String[] ownerMobiles = buildEnginer.getOwnerMobile().split(",");
		if (ownerNames.length != ownerMobiles.length) {
			throw new BadRequestException("业主代表输入格式有误！");
		}
		boolean ob = Stream.of(ownerNames).anyMatch(on -> StrUtil.isBlank(on));
		if (ob) {
			throw new BadRequestException("业主代表名称不能为空！");
		}
		boolean o = Stream.of(ownerNames).anyMatch(on -> on.length() > 12);
		if (o) {
			throw new BadRequestException("业主代表名称长度不能超过12位！");
		}
		boolean mb = Stream.of(ownerMobiles).anyMatch(om -> !CommonUtil.checkMobile(om));
		if (mb) {
			throw new BadRequestException("业主代表手机号码不合法！");
		}

		String[] devNames = buildEnginer.getDevName().split(",");
		String[] devMobiles = buildEnginer.getDevMobile().split(",");
		if (devNames.length != devMobiles.length) {
			throw new BadRequestException("开发经理输入格式有误！");
		}
		boolean db = Stream.of(devNames).anyMatch(dn -> StrUtil.isBlank(dn));
		if (db) {
			throw new BadRequestException("开发经理名称不能为空！");
		}
		boolean d = Stream.of(devNames).anyMatch(dn -> dn.length() > 12);
		if (d) {
			throw new BadRequestException("开发经理名称长度不能超过12位！");
		}
		boolean dmb = Stream.of(devMobiles).anyMatch(dm -> !CommonUtil.checkMobile(dm));
		if (dmb) {
			throw new BadRequestException("开发经理手机号码不合法！");
		}

		String projectName = buildEnginer.getProjectName();
		String projectMobile = buildEnginer.getProjectMobile();
		if (StrUtil.isNotBlank(projectName) && StrUtil.isBlank(projectMobile)) {
			throw new BadRequestException("项目经理手机号码不能为空！");
		}
		if (StrUtil.isBlank(projectName) && StrUtil.isNotBlank(projectMobile)) {
			throw new BadRequestException("项目经理名称不能为空！");
		}
		if (StrUtil.isNotBlank(projectName) && StrUtil.isNotBlank(projectMobile)) {
			String[] projectNames = projectName.split(",");
			String[] projectMobiles = projectMobile.split(",");
			if (projectNames.length != projectMobiles.length) {
				throw new BadRequestException("项目经理输入格式有误！");
			}
			boolean pb = Stream.of(projectNames).anyMatch(pn -> StrUtil.isBlank(pn));
			if (pb) {
				throw new BadRequestException("项目经理名称不能为空！");
			}
			boolean p = Stream.of(projectNames).anyMatch(pn -> pn.length() > 12);
			if (p) {
				throw new BadRequestException("项目经理名称长度不能超过12位！");
			}
			boolean pmb = Stream.of(projectMobiles).anyMatch(pm -> !CommonUtil.checkMobile(pm));
			if (pmb) {
				throw new BadRequestException("项目经理手机号码不合法！");
			}

			boolean anyMatch = Stream.of(projectMobiles)
					.anyMatch(pm -> Stream.of(seriesMobiles).anyMatch(sm -> StrUtil.equals(pm, sm)));
			if (anyMatch) {
				throw new BadRequestException("项目经理手机号码与工程经理手机号码存在重复！");
			}

			if (StrUtil.isNotBlank(delSeriesMobile)) {
				String[] delSeriesMobiles = delSeriesMobile.split(",");
				int delSmLength = delSeriesMobiles.length;
				for (String sm : delSeriesMobiles) {
					for (int i = 0; i < projectMobiles.length; i++) {
						if (StrUtil.equals(sm, projectMobiles[i])) {
							String serMob = projectMobiles[i];
							if (delSmLength > 1) {
								serMob = "," + serMob;
							}
							delSeriesMobile = delSeriesMobile.replaceAll(serMob, "");
						}
					}
				}
			}
			if (StrUtil.isNotBlank(delProjectMobile)) {
				String[] delProjectMobiles = delProjectMobile.split(",");
				int delPmLength = delProjectMobiles.length;
				for (String pm : delProjectMobiles) {
					for (int i = 0; i < projectMobiles.length; i++) {
						if (StrUtil.equals(pm, projectMobiles[i])) {
							String perMob = projectMobiles[i];
							if (delPmLength > 1) {
								perMob = "," + perMob;
							}
							delProjectMobile = delProjectMobile.replaceAll(perMob, "");
						}
					}
				}
			}
		}

		buildEnginer.setDelSeriesMobile(delSeriesMobile);
		buildEnginer.setDelProjectMobile(delProjectMobile);
	}

	/**
	 * @dec 修改工程
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildEnginer
	 * @param token
	 * @return
	 */
	@PutMapping
	public R edit(@Valid BuildEnginer buildEnginer, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		validate(buildEnginer);

		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		boolean hasEdit = false;
		if (userOrg != null) {
			hasEdit = true;
		}

		if (!hasEdit) {
			QueryWrapper<UserDoorway> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("doorway_id", buildEnginer.getDoorwayId()).and(i -> i.eq("user_id", json.getStr("id")));
			int count = userDoorwayService.count(queryWrapper);
			if (count > 0) {
				hasEdit = true;
			}
		}

		if (!hasEdit) {
			throw new ForbiddenException();
		}

		LocalDate prepareDate = buildEnginer.getPrepareDate();
		LocalDate now = LocalDate.now();
		BuildEnginer old = buildEnginerService.getById(buildEnginer.getId());
		LocalDate oldDate = old.getPrepareDate();
		if ((oldDate.isBefore(now) || oldDate.isEqual(now)) && !oldDate.isEqual(prepareDate)) {
			return R.error("该工程已经开始筹建，不能修改筹建日期！");
		} else {
			Doorway doorway = doorwayService.getOne("id", SqlEmnus.EQ, buildEnginer.getDoorwayId(), "id", "status");
			if (prepareDate.isBefore(now) || prepareDate.isEqual(now)) {
				buildEnginer.setStatus(BuildEnginer.STATUS_DOING);
				doorway.setStatus(Doorway.STATUS_DOING);
				doorwayService.updateById(doorway);
			}
		}

		return buildEnginerService.editBuildEnginer(buildEnginer, json, old);
	}

	/**
	 * @dec 关闭/开启通知
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@PostMapping("/colse")
	public R close(String id, Integer remind, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		return buildEnginerService.close(id, token, remind);
	}

	/**
	 * @dec 获取周报日期列表
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param id
	 * @param month
	 * @return
	 */
	@GetMapping("/weekly/{id}")
	public R weekly(@PathVariable("id") String id, Integer month,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		Map<String, Object> data = new HashMap<>(5);
		Doorway doorway = doorwayService.getOne("id", SqlEmnus.EQ, id, "id", "category_id");
		DoorwayCategory doorwayCategory = doorwayCategoryService.getOne("id", SqlEmnus.EQ, doorway.getCategoryId(),
				"id", "val");

		if (DoorwayCategory.VAL_BUILD == doorwayCategory.getVal()) {
			BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, id);
			QueryWrapper<BuildProject> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("type", BuildProject.TYPE_IN).and(i -> i.eq("build_enginer_id", buildEnginer.getId()));
			int sumCount = buildProjectService.count(queryWrapper);
			data.put("sumCount", sumCount);
			queryWrapper.and(i -> i.eq("status", BuildProject.STATUS_FLUSH));
			int count = buildProjectService.count(queryWrapper);
			data.put("count", count);

			List<Integer> weekMonth = buildProjectLogService.weekMonth();
			if (CollUtil.isEmpty(weekMonth)) {
				data.put("weekMonth", new ArrayList<>(0));
			} else {
				data.put("weekMonth", weekMonth);
			}

			List<String> list = buildProjectService.list("build_enginer_id", SqlEmnus.EQ, buildEnginer.getId(), "id")
					.stream().map(BuildProject::getId).collect(Collectors.toList());
			Map<String, Object> params = new HashMap<>(3);
			params.put("ids", list);
			params.put("month", month);
			params.put("userId", CommonUtil.decodeToken(token).getStr("id"));
			List<Map<String, Object>> weekTimes = buildProjectLogService.weekTime(params);
			data.put("list", weekTimes);

			return R.ok(data);
		}

		return R.error();
	}

	/**
	 * @dec 获取周报数据
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param id
	 * @param beginDate
	 * @param endDate
	 * @param token
	 * @return
	 */
	@GetMapping("/weekly/{id}/detail")
	public R weeklyDetail(@PathVariable("id") String id, String beginDate, String endDate,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		if (StrUtil.isBlank(beginDate) || StrUtil.isBlank(endDate)) {
			throw new BadRequestException();
		}

		String startDate = beginDate.replaceAll("\\.", "-");
		String endingDate = endDate.replaceAll("\\.", "-") + " 23:59:59";
		Doorway doorway = doorwayService.getOne("id", SqlEmnus.EQ, id, "category_id");
		DoorwayCategory doorwayCategory = doorwayCategoryService.getOne("id", SqlEmnus.EQ, doorway.getCategoryId(),
				"val");

		if (DoorwayCategory.VAL_BUILD == doorwayCategory.getVal()) {
			BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, id);
			List<String> list = buildProjectService.list("build_enginer_id", SqlEmnus.EQ, buildEnginer.getId(), "id")
					.stream().map(BuildProject::getId).collect(Collectors.toList());

			QueryWrapper<BuildProjectLog> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("user_id", CommonUtil.decodeToken(token).getStr("id"));
			queryWrapper.and(i -> i.in("build_project_id", list)).and(i -> i.ge("create_time", startDate));
			queryWrapper.and(i -> i.le("create_time", endingDate)).orderByDesc("create_time");
			List<BuildProjectLog> bpls = buildProjectLogService.list(queryWrapper);
			if (CollUtil.isEmpty(bpls)) {
				return R.ok(new ArrayList<>(0));
			}

			List<Map<String, Object>> collect = buildProjectService.filterData(bpls);

			return R.ok(collect);
		}

		return R.error();
	}

	/**
	 * @dec 生成周报
	 * @date Mar 15, 2019
	 * @author gaochao
	 * @param id
	 * @param email
	 * @param beginDate
	 * @param endDate
	 * @param token
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/weekly/{id}/send")
	public R weeklySend(@PathVariable("id") String id, String email, String beginDate, String endDate,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws Exception {
		if (StrUtil.isBlank(beginDate) || StrUtil.isBlank(endDate) || StrUtil.isBlank(email)) {
			throw new BadRequestException();
		}

		if (!CommonUtil.checkEmail(email)) {
			throw new BadRequestException("邮箱不合法！");
		}

		JSONObject jo = CommonUtil.decodeToken(token);
		String startDate = beginDate.replaceAll("\\.", "-");
		String endingDate = endDate.replaceAll("\\.", "-") + " 23:59:59";
		Doorway doorway = doorwayService.getOne("id", SqlEmnus.EQ, id, "id", "name", "category_id");
		DoorwayCategory doorwayCategory = doorwayCategoryService.getOne("id", SqlEmnus.EQ, doorway.getCategoryId(),
				"val");

		if (DoorwayCategory.VAL_BUILD == doorwayCategory.getVal()) {
			BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, id, "id");
			List<String> list = buildProjectService.list("build_enginer_id", SqlEmnus.EQ, buildEnginer.getId(), "id")
					.stream().map(BuildProject::getId).collect(Collectors.toList());

			QueryWrapper<BuildProjectLog> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("user_id", jo.getStr("id"));
			queryWrapper.and(i -> i.in("build_project_id", list)).and(i -> i.ge("create_time", startDate));
			queryWrapper.and(i -> i.le("create_time", endingDate));
			List<BuildProjectLog> bpls = buildProjectLogService.list(queryWrapper);
			if (CollUtil.isEmpty(bpls)) {
				return R.error("不可以生成空的周报！");
			}

			User user = userService.getOne("id", SqlEmnus.EQ, jo.getStr("id"), "username", "real_name");
			String title = doorway.getName() + user.getRealName() + user.getUsername() + "周报";

			return buildEnginerService.sendEmail(email, bpls, title);
		}

		return R.error();
	}

	/**
	 * @dec 获取预计竣工时间
	 * @date Mar 17, 2019
	 * @author gaochao
	 * @param prepareDate
	 * @param doorwayId
	 * @return
	 */
	@GetMapping("/date")
	public R getForecastDate(String prepareDate, String doorwayId) {
		LocalDate parse = LocalDate.parse(prepareDate);
		Doorway doorway = doorwayService.getOne("id", SqlEmnus.EQ, doorwayId, "org_id", "category_id");
		QueryWrapper<DoorwayModel> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("doorway_id", doorwayId).and(i -> i.eq("category_id", doorway.getCategoryId()))
				.select("model_id");
		List<String> modelIds = doorwayModelService.list(queryWrapper).stream().map(DoorwayModel::getModelId)
				.collect(Collectors.toList());
		int maxDays = modelBuildService.getMaxDays(modelIds);

		LocalDate end = parse.plusDays(maxDays);
		return R.ok(end.toString());
	}

	/**
	 * @dec 获取设计标准下拉菜单
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param token
	 * @return
	 */
	@GetMapping("/design")
	public R design(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		if (userOrg == null) {
			throw new ForbiddenException();
		}

		QueryWrapper<Doorway> doorwayQuery = new QueryWrapper<>();
		doorwayQuery.eq("org_id", userOrg.getOrgId()).select("id");
		List<Doorway> list = doorwayService.list(doorwayQuery);
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		List<String> doorwayIds = list.stream().map(Doorway::getId).collect(Collectors.toList());
		QueryWrapper<BuildEnginer> queryWrapper = new QueryWrapper<>();
		queryWrapper.in("doorway_id", doorwayIds).select("design").groupBy("design");
		List<String> designs = buildEnginerService.list(queryWrapper).stream().map(BuildEnginer::getDesign)
				.collect(Collectors.toList());

		return R.ok(designs);
	}

}
