package com.suizhu.work.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.MyException;
import com.suizhu.work.entity.Organization;
import com.suizhu.work.entity.User;
import com.suizhu.work.user.service.OrganizationService;
import com.suizhu.work.user.service.UserOrgService;
import com.suizhu.work.user.service.UserService;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-28
 */
@RestController
@AllArgsConstructor
@RequestMapping("user")
public class UserController {

	private final UserService userService;

	private final OrganizationService organizationService;

	private final UserOrgService userOrgService;

	/**
	 * @dec 跳转至用户列表页面
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/gotoList")
	public ModelAndView gotoList() {
		return new ModelAndView("user/list");
	}

	/**
	 * @dec 查询用户列表
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params) {
		return userService.queryList(params);
	}

	/**
	 * @dec 跳转至用户新增或修改页面
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/gotoInfo")
	public ModelAndView gotoInfo(String id) {
		ModelAndView mav = new ModelAndView("user/info");

		QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id", "name");
		List<Map<String, Object>> list = organizationService.list(queryWrapper).stream().map(org -> {
			Map<String, Object> data = new HashMap<>(2);
			data.put("id", org.getId());
			data.put("name", org.getName());
			return data;
		}).collect(Collectors.toList());
		mav.addObject("orgs", list);

		if (StrUtil.isNotBlank(id)) {
			User user = userService.getById(id);
			if (user == null) {
				throw new MyException("非法请求！");
			}

			String orgId = userOrgService.getOne("user_id", SqlEmnus.EQ, id, "org_id").getOrgId();
			user.setOrgId(orgId);
			mav.addObject("info", user);
		}

		return mav;
	}

	/**
	 * @dec 验证账号
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param id
	 * @param username
	 * @return
	 */
	@PostMapping("/checkUsername")
	public R checkUsername(String id, String username) {
		boolean exist = userService.checkOnly(id, "username", username);
		return R.ok(!exist);
	}

	/**
	 * @dec 验证邮箱
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param id
	 * @param email
	 * @return
	 */
	@PostMapping("/checkEmail")
	public R checkEmail(String id, String email) {
		boolean exist = userService.checkOnly(id, "email", email);
		return R.ok(!exist);
	}

	/**
	 * @dec 新增用户
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param user
	 * @return
	 */
	@PostMapping
	public R save(@Valid @RequestBody User user) {
		boolean checkUseranme = userService.exist("username", SqlEmnus.EQ, user.getUsername());
		if (checkUseranme) {
			return R.error("该账号已存在！");
		}
		if (StrUtil.isNotBlank(user.getEmail())) {
			boolean matches = user.getEmail().matches("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
			if (!matches) {
				return R.error("邮箱不合法！");
			}

			boolean exist = userService.exist("email", SqlEmnus.EQ, user.getEmail());
			if (exist) {
				return R.error("该邮箱已存在！");
			}
		}
		if (StrUtil.isBlank(user.getOrgId())) {
			return R.error("请选择所属公司！");
		}

		return userService.addUser(user);
	}

	/**
	 * @dec 修改用户
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param user
	 * @return
	 */
	@PutMapping
	public R edit(@Valid @RequestBody User user) {
		boolean checkUsername = userService.checkOnly(user.getId(), "username", user.getUsername());
		if (!checkUsername) {
			return R.error("该账号已存在！");
		}
		if (StrUtil.isNotBlank(user.getEmail())) {
			boolean matches = user.getEmail().matches("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
			if (!matches) {
				return R.error("邮箱不合法！");
			}

			boolean checkEmail = userService.checkOnly(user.getId(), "email", user.getEmail());
			if (!checkEmail) {
				return R.error("该邮箱已存在！");
			}
		}

		boolean update = userService.updateById(user);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 禁用或启用用户
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param param
	 * @return
	 */
	@PutMapping("/usable")
	public R usable(@RequestBody Map<String, Object> param) {
		String id = MapUtil.getStr(param, "id");
		Integer status = MapUtil.getInt(param, "status");
		if (StrUtil.isBlank(id) || status == null) {
			return R.error("非法请求！");
		}

		return userService.usable(id, status);
	}

}
