package com.suizhu.cms.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.suizhu.cms.entity.SysRole;
import com.suizhu.cms.entity.SysUser;
import com.suizhu.cms.service.SysRoleService;
import com.suizhu.cms.service.SysUserService;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.MyException;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 后台用户表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("sysUser")
public class SysUserController {

	private final SysUserService sysUserService;

	private final SysRoleService sysRoleService;

	/**
	 * @dec 跳转至后台用户列表页面
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/gotoList")
	public ModelAndView gotoList() {
		return new ModelAndView("sys/user/list");
	}

	/**
	 * @dec 获取后台用户列表
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params) {
		return sysUserService.queryList(params);
	}

	/**
	 * @dec 跳转至后台用户新增或修改页面
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/gotoInfo")
	public ModelAndView gotoInfo(String id) {
		ModelAndView mav = new ModelAndView("sys/user/info");
		List<SysRole> sysRoles = sysRoleService.list("status", SqlEmnus.EQ, SysRole.STATUS_NORMAL, "id", "name");
		mav.addObject("sysRoles", getSysRoles(sysRoles, null));

		if (StrUtil.isNotBlank(id)) {
			SysUser sysUser = sysUserService.getById(id);
			if (sysUser == null) {
				throw new MyException("非法请求！");
			}

			List<SysRole> list = sysRoleService.findBySysUserId(id);
			mav.addObject("sysRoles", getSysRoles(sysRoles, list));
			mav.addObject("info", sysUser);
		}

		return mav;
	}

	/**
	 * @dec 获取用户所属角色
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysRoles
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> getSysRoles(List<SysRole> sysRoles, List<SysRole> list) {
		List<Map<String, Object>> roles = sysRoles.stream().map(r -> {
			Map<String, Object> data = new LinkedHashMap<>(3);
			data.put("id", r.getId());
			data.put("name", r.getName());

			boolean checked = false;
			if (CollUtil.isNotEmpty(list)) {
				checked = list.stream().anyMatch(i -> StrUtil.equals(r.getId(), i.getId()));
			}
			data.put("checked", checked);

			return data;
		}).collect(Collectors.toList());

		return roles;
	}

	/**
	 * @dec 验证账号是否存在
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param userName
	 * @param sysUserId
	 * @return
	 */
	@PostMapping("/checkUserName")
	public R checkUserName(String userName, String sysUserId) {
		boolean checkOnly = sysUserService.checkOnly(sysUserId, "username", userName);
		return R.ok(!checkOnly);
	}

	/**
	 * @dec 添加后台用户
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysUser
	 * @return
	 */
	@PostMapping
	public R save(@Valid @RequestBody SysUser sysUser) {
		boolean checkUsername = sysUserService.exist("username", SqlEmnus.EQ, sysUser.getUsername());
		if (checkUsername) {
			return R.error("该账号已被使用！");
		}
		boolean checkNickName = sysUserService.exist("nick_name", SqlEmnus.EQ, sysUser.getNickName());
		if (checkNickName) {
			return R.error("该昵称已被使用！");
		}
		boolean checkMobile = sysUserService.exist("mobile", SqlEmnus.EQ, sysUser.getMobile());
		if (checkMobile) {
			return R.error("该手机号已存在！");
		}

		return sysUserService.add(sysUser);
	}

	/**
	 * @dec 修改后台用户
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysUser
	 * @return
	 */
	@PutMapping
	public R edit(@Valid @RequestBody SysUser sysUser) {
		String id = sysUser.getId();
		boolean checkUsername = sysUserService.checkOnly(id, "username", sysUser.getUsername());
		if (!checkUsername) {
			return R.error("该账号已被使用！");
		}
		boolean checkNickName = sysUserService.checkOnly(id, "nick_name", sysUser.getNickName());
		if (!checkNickName) {
			return R.error("该昵称已被使用！");
		}
		boolean checkMobile = sysUserService.checkOnly(id, "mobile", sysUser.getMobile());
		if (!checkMobile) {
			return R.error("该手机号已存在！");
		}

		return sysUserService.edit(sysUser);
	}

	/**
	 * @dec 删除后台用户
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@DeleteMapping
	public R del(String id) {
		if (StrUtil.isBlank(id)) {
			return R.error("非法请求！");
		}
		String[] ids = id.split(",");
		if (ArrayUtil.isEmpty(ids)) {
			return R.error("非法请求！");
		}

		ArrayList<String> idList = CollUtil.toList(ids);
		return sysUserService.del(idList);
	}

	/**
	 * @dec 启用或者禁用后台用户
	 * @date Feb 21, 2019
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

		SysUser sysUser = sysUserService.getById(id);
		sysUser.setStatus(status);
		boolean update = sysUserService.updateById(sysUser);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

}
