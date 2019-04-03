package com.suizhu.work.user.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.suizhu.work.entity.UserOrg;
import com.suizhu.work.user.service.UserOrgService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 公司人员表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-13
 */
@RestController
@AllArgsConstructor
@RequestMapping("userOrg")
public class UserOrgController {

	private final UserOrgService userOrgService;

	/**
	 * @dec 获取人员管理列表
	 * @date Mar 13, 2019
	 * @author gaochao
	 * @param token
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		if (userOrg == null) {
			throw new ForbiddenException();
		}

		List<UserOrg> list = userOrgService.list("org_id", SqlEmnus.EQ, userOrg.getOrgId());
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		List<Map<String, Object>> userOrgs = list.stream().map(uo -> {
			Map<String, Object> data = CommonUtil.beanToMapFl(uo, "userId", "orgId", "createTime", "updateTime");
			return data;
		}).collect(Collectors.toList());

		return R.ok(userOrgs);
	}

	/**
	 * @dec 新增公司人员
	 * @date Mar 13, 2019
	 * @author gaochao
	 * @param userOrgs
	 * @param token
	 * @return
	 */
	@PostMapping
	public R save(String userOrgs, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		if (StrUtil.isBlank(userOrgs)) {
			throw new BadRequestException();
		}

		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		if (userOrg == null) {
			throw new ForbiddenException();
		}

		List<UserOrg> list = JSONUtil.parseArray(userOrgs).toList(UserOrg.class);

		return userOrgService.add(list, userOrg, json);
	}

	/**
	 * @dec 修改公司人员
	 * @date Mar 13, 2019
	 * @author gaochao
	 * @param userOrg
	 * @param token
	 * @return
	 */
	@PutMapping
	public R edit(@Valid UserOrg userOrg, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg uo = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		if (uo == null) {
			throw new ForbiddenException();
		}

		userOrg.setOrgId(uo.getOrgId());
		return userOrgService.edit(userOrg, json);
	}

	/**
	 * @dec 删除公司人员
	 * @date Mar 13, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@DeleteMapping("/{id}")
	public R del(@PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg uo = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		if (uo == null) {
			throw new ForbiddenException();
		}

		return userOrgService.del(id, json);
	}

	/**
	 * @dec 获取人员管理列表
	 * @date Mar 27, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param token
	 * @return
	 */
	@GetMapping("/v2")
	public R queryListV2(String condition, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		if (userOrg == null) {
			throw new ForbiddenException();
		}

		QueryWrapper<UserOrg> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("org_id", userOrg.getOrgId());
		if (StrUtil.isNotBlank(condition)) {
			queryWrapper.and(i -> i.like("work_kind", condition).or(m -> m.like("mobile", condition))
					.or(n -> n.like("name", condition)).or(wn -> wn.like("work_num", condition))
					.or(e -> e.like("email", condition)));
		}

		List<UserOrg> list = userOrgService.list(queryWrapper);
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		Set<String> deps = list.stream().map(UserOrg::getDep).collect(Collectors.toSet());
		List<Map<String, Object>> datas = new ArrayList<>(deps.size());
		deps.forEach(d -> {
			Map<String, Object> re = new HashMap<>(2);
			re.put("dep", d);

			List<Map<String, Object>> userOrgs = new ArrayList<>();
			list.stream().sorted((Comparator<UserOrg>) (c1, c2) -> {
				if (StrUtil.equals(c1.getDep(), UserOrg.DEFAULT_DEP)
						|| StrUtil.equals(c2.getDep(), UserOrg.DEFAULT_DEP)) {
					return -1;
				}

				return c1.getDep().compareTo(c2.getDep());
			}).forEachOrdered(uo -> {
				if (StrUtil.equals(uo.getDep(), d)) {
					Map<String, Object> data = CommonUtil.beanToMapFl(uo, "userId", "orgId", "createTime",
							"updateTime");
					userOrgs.add(data);
				}
			});
			re.put("userOrgs", userOrgs);

			datas.add(re);
		});

		return R.ok(datas);
	}

	/**
	 * @dec 获取人员管理下拉菜单
	 * @date Mar 28, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param token
	 * @return
	 */
	@GetMapping("/option")
	public R option(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		if (userOrg == null) {
			throw new ForbiddenException();
		}

		Map<String, List<String>> data = new HashMap<>(3);

		QueryWrapper<UserOrg> wrapper = new QueryWrapper<>();
		wrapper.eq("org_id", userOrg.getOrgId()).and(i -> i.isNotNull("dep"));
		wrapper.and(i -> i.ne("dep", "")).select("dep").groupBy("dep");
		List<UserOrg> deps = userOrgService.list(wrapper);
		if (CollUtil.isEmpty(deps)) {
			data.put("deps", new ArrayList<>(0));
		} else {
			List<String> depss = deps.stream().map(UserOrg::getDep).collect(Collectors.toList());
			data.put("deps", depss);
		}

		wrapper = new QueryWrapper<>();
		wrapper.eq("org_id", userOrg.getOrgId()).and(i -> i.isNotNull("work_kind"));
		wrapper.and(i -> i.ne("work_kind", "")).select("work_kind").groupBy("work_kind");
		List<UserOrg> wks = userOrgService.list(wrapper);
		if (CollUtil.isEmpty(wks)) {
			data.put("wks", new ArrayList<>(0));
		} else {
			List<String> wkss = wks.stream().map(UserOrg::getWorkKind).collect(Collectors.toList());
			data.put("wks", wkss);
		}

		wrapper = new QueryWrapper<>();
		wrapper.eq("org_id", userOrg.getOrgId()).and(i -> i.isNotNull("pos"));
		wrapper.and(i -> i.ne("pos", "")).select("pos").groupBy("pos");
		List<UserOrg> poss = userOrgService.list(wrapper);
		if (CollUtil.isEmpty(poss)) {
			data.put("poss", new ArrayList<>(0));
		} else {
			List<String> posss = poss.stream().map(UserOrg::getPos).collect(Collectors.toList());
			data.put("poss", posss);
		}

		return R.ok(data);
	}

}
