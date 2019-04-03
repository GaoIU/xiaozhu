package com.suizhu.work.user.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.suizhu.work.build.service.BuildTipService;
import com.suizhu.work.entity.BuildTip;
import com.suizhu.work.entity.UserDoorway;
import com.suizhu.work.user.service.UserDoorwayService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 用户表 - 门店表 中间关联表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-11
 */
@RestController
@AllArgsConstructor
@RequestMapping("userDoorway")
public class UserDoorwayController {

	private final UserDoorwayService userDoorwayService;

	private final BuildTipService buildTipService;

	private final BuildEnginerService buildEnginerService;

	/**
	 * @dec 新增人员
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param userDoorways
	 * @param doorwayId
	 * @param token
	 * @return
	 */
	@PostMapping
	public R save(String userDoorways, String doorwayId, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		if (StrUtil.isBlank(userDoorways) || StrUtil.isBlank(doorwayId)) {
			throw new BadRequestException();
		}

		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
		udqw.eq("doorway_id", doorwayId).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(udqw);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		List<UserDoorway> list = JSONUtil.parseArray(userDoorways).toList(UserDoorway.class);

		return userDoorwayService.add(list, doorwayId, json);
	}

	/**
	 * @dec 修改人员
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param userDoorway
	 * @param token
	 * @return
	 */
	@PutMapping
	public R edit(@Valid UserDoorway userDoorway, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
		udqw.eq("doorway_id", userDoorway.getDoorwayId()).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(udqw);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		UserDoorway ud = userDoorwayService.getOne("id", SqlEmnus.EQ, userDoorway.getId(), "mobile", "email");
		QueryWrapper<UserDoorway> mobileQuery = new QueryWrapper<>();
		mobileQuery.eq("mobile", userDoorway.getMobile()).and(i -> i.eq("doorway_id", userDoorway.getDoorwayId()));
		int mobileCount = userDoorwayService.count(mobileQuery);
		if (mobileCount > 0) {
			if (!StrUtil.equals(ud.getMobile(), userDoorway.getMobile())) {
				throw new BadRequestException("该手机号码已被使用！");
			}
		}

		if (StrUtil.isNotBlank(userDoorway.getEmail())) {
			if (!CommonUtil.checkEmail(userDoorway.getEmail())) {
				throw new BadRequestException("存在不合法邮箱！请核对后重试");
			}

			QueryWrapper<UserDoorway> emailQuery = new QueryWrapper<>();
			emailQuery.eq("email", userDoorway.getEmail()).and(i -> i.eq("doorway_id", userDoorway.getDoorwayId()));
			int emailCount = userDoorwayService.count(emailQuery);
			if (emailCount > 0) {
				if (!StrUtil.equals(ud.getEmail(), userDoorway.getEmail())) {
					throw new BadRequestException("该邮箱已被使用！");
				}
			}
		}

		return userDoorwayService.edit(userDoorway, json);
	}

	/**
	 * @dec 删除人员
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	public R del(@PathVariable("id") String id) {
		UserDoorway userDoorway = userDoorwayService.getOne("id", SqlEmnus.EQ, id, "id", "user_id", "doorway_id");
		boolean remove = userDoorwayService.removeById(id);
		if (remove) {
			String buildEnginerId = buildEnginerService
					.getOne("doorway_id", SqlEmnus.EQ, userDoorway.getDoorwayId(), "id").getId();
			QueryWrapper<BuildTip> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("user_id", userDoorway.getUserId()).and(i -> i.eq("build_enginer_id", buildEnginerId));
			buildTipService.remove(queryWrapper);

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 获取人员管理下拉菜单
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param doorwayId
	 * @return
	 */
	@GetMapping("/option")
	public R option(String doorwayId) {
		Map<String, List<String>> data = new HashMap<>(3);

		QueryWrapper<UserDoorway> wrapper = new QueryWrapper<>();
		wrapper.eq("doorway_id", doorwayId).and(i -> i.isNotNull("dep"));
		wrapper.and(i -> i.ne("dep", "")).select("dep").groupBy("dep");
		List<UserDoorway> deps = userDoorwayService.list(wrapper);
		if (CollUtil.isEmpty(deps)) {
			data.put("deps", new ArrayList<>(0));
		} else {
			List<String> depss = deps.stream().map(UserDoorway::getDep).collect(Collectors.toList());
			data.put("deps", depss);
		}

		wrapper = new QueryWrapper<>();
		wrapper.eq("doorway_id", doorwayId).and(i -> i.isNotNull("work_kind"));
		wrapper.and(i -> i.ne("work_kind", "")).select("work_kind").groupBy("work_kind");
		List<UserDoorway> wks = userDoorwayService.list(wrapper);
		if (CollUtil.isEmpty(wks)) {
			data.put("wks", new ArrayList<>(0));
		} else {
			List<String> wkss = wks.stream().map(UserDoorway::getWorkKind).collect(Collectors.toList());
			data.put("wks", wkss);
		}

		wrapper = new QueryWrapper<>();
		wrapper.eq("doorway_id", doorwayId).and(i -> i.isNotNull("pos"));
		wrapper.and(i -> i.ne("pos", "")).select("pos").groupBy("pos");
		List<UserDoorway> poss = userDoorwayService.list(wrapper);
		if (CollUtil.isEmpty(poss)) {
			data.put("poss", new ArrayList<>(0));
		} else {
			List<String> posss = poss.stream().map(UserDoorway::getPos).collect(Collectors.toList());
			data.put("poss", posss);
		}

		return R.ok(data);
	}

}
