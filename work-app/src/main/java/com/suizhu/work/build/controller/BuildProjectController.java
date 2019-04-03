package com.suizhu.work.build.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.common.exception.ForbiddenException;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.UserDoorway;
import com.suizhu.work.user.service.UserDoorwayService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 筹建项目表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("buildProject")
public class BuildProjectController {

	private final BuildProjectService buildProjectService;

	private final UserDoorwayService userDoorwayService;

	/**
	 * @dec 新增工序
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildProject
	 * @param token
	 * @return
	 * @since 2.0.1
	 */
	@PostMapping
	public R save(@Valid BuildProject buildProject, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
		udqw.eq("type", UserDoorway.TYPE_DOORWAY).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(udqw);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		if (StrUtil.isNotBlank(buildProject.getSeriesName())) {
			if (StrUtil.isBlank(buildProject.getSeriesMobile())) {
				throw new BadRequestException("工序组长手机号码不能为空！");
			}
		}

		if (StrUtil.isNotBlank(buildProject.getSeriesMobile())) {
			boolean checkMobile = CommonUtil.checkMobile(buildProject.getSeriesMobile());
			if (!checkMobile) {
				throw new BadRequestException("工序组长手机号码不合法！");
			}

			if (StrUtil.isBlank(buildProject.getSeriesName())) {
				throw new BadRequestException("工序组长不能为空！");
			}
		}

		buildProject.setCreateId(json.getStr("id"));
		QueryWrapper<BuildProject> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("build_enginer_id", buildProject.getBuildEnginerId()).select("sort");
		if (StrUtil.isBlank(buildProject.getParentId())) {
			queryWrapper.and(i -> i.isNull("parent_id").or(o -> o.eq("parent_id", "")));
		} else {
			queryWrapper.and(i -> i.eq("parent_id", buildProject.getParentId()));
		}
		Optional<BuildProject> optional = buildProjectService.list(queryWrapper).stream()
				.max(Comparator.comparingInt(BuildProject::getSort));
		buildProject.setSort(optional.get().getSort() + 1);

		LocalDate now = LocalDate.now();
		LocalDate beginDate = buildProject.getBeginDate();
		LocalDate endDate = buildProject.getEndDate();
		if (beginDate.isBefore(now) || beginDate.isEqual(now)) {
			buildProject.setStatus(BuildProject.STATUS_DOING);

			long abs = Math.abs(ChronoUnit.DAYS.between(beginDate, now));
			if (abs >= 2) {
				buildProject.setEarly(BuildProject.EARLY_WARNING);
			}
		}
		if (endDate.isBefore(now)) {
			buildProject.setEarly(BuildProject.EARLY_OVERTIME_DOING);
		}

		return buildProjectService.add(buildProject, json);
	}

	/**
	 * @dec 删除工序
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@DeleteMapping("/{id}")
	public R del(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		BuildProject buildProject = buildProjectService.getById(id);
		if (buildProject == null) {
			throw new BadRequestException();
		}

		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
		udqw.eq("type", UserDoorway.TYPE_DOORWAY).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(udqw);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		return buildProjectService.del(buildProject, json);
	}

	/**
	 * @dec 获取项目日志列表
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@GetMapping("/{id}")
	public R info(@PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject jo = CommonUtil.decodeToken(token);
		return R.ok(buildProjectService.info(id, jo.getStr("id")));
	}

	/**
	 * @dec 获取月份选项卡
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}/option")
	public R option(@PathVariable("id") String id) {
		List<Integer> list = buildProjectService.option(id);
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		return R.ok(list);
	}

	/**
	 * @dec 查看项目列表
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping("/view")
	public R view(@RequestParam Map<String, Object> params, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		params.replace("month", MapUtil.getInt(params, "month"));
		params.replace("status", MapUtil.getInt(params, "status"));
		List<Map<String, Object>> list = buildProjectService.view(params, token);
		return R.ok(list);
	}

	/**
	 * @dec 获取工序组长列表
	 * @date Mar 28, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param seriesId
	 * @return
	 */
	@GetMapping("/sernice")
	public R sernice(String seriesId) {
		if (StrUtil.isBlank(seriesId)) {
			return R.ok(new ArrayList<>(0));
		}

		List<String> udIds = new ArrayList<>(Arrays.asList(seriesId.split(",")));
		QueryWrapper<UserDoorway> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("type", UserDoorway.TYPE_PROJECT).and(i -> i.in("id", udIds));
		List<UserDoorway> list = userDoorwayService.list(queryWrapper);
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		List<Map<String, Object>> datas = list.stream().map(ud -> {
			Map<String, Object> data = new HashMap<>(9);
			data.put("id", ud.getId());
			data.put("mobile", ud.getMobile());
			data.put("name", ud.getName());
			data.put("workNum", ud.getWorkNum());
			data.put("email", ud.getEmail());
			data.put("dep", ud.getDep());
			data.put("pos", ud.getPos());
			data.put("workKind", ud.getWorkKind());
			data.put("workOut", ud.getWorkOut());
			return data;
		}).collect(Collectors.toList());

		return R.ok(datas);
	}

	/**
	 * @dec 分配组长
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param userId
	 * @param token
	 * @return
	 */
	@PostMapping("setSernice")
	public R setSernice(String id, String userDoorwayId, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		if (StrUtil.isBlank(id) || StrUtil.isBlank(userDoorwayId)) {
			throw new BadRequestException();
		}

		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
		udqw.eq("type", UserDoorway.TYPE_DOORWAY).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(udqw);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		return buildProjectService.setSernice(id, userDoorwayId);
	}

	/**
	 * @dec 删除工序组长
	 * @date Mar 28, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param userDoorwayId
	 * @param token
	 * @return
	 */
	@DeleteMapping("/sernice")
	public R delSernice(String id, String userDoorwayId, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		if (StrUtil.isBlank(id) || StrUtil.isBlank(userDoorwayId)) {
			throw new BadRequestException();
		}

		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
		udqw.eq("type", UserDoorway.TYPE_DOORWAY).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(udqw);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		return buildProjectService.delSernice(id, userDoorwayId);
	}

}
