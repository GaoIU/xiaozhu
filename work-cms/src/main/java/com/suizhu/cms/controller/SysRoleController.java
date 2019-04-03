package com.suizhu.cms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.cms.entity.SysResource;
import com.suizhu.cms.entity.SysRole;
import com.suizhu.cms.service.SysResourceService;
import com.suizhu.cms.service.SysRoleService;
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
 * 后台角色表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("sysRole")
public class SysRoleController {

	private final SysRoleService sysRoleService;

	private final SysResourceService sysResourceService;

	/**
	 * @dec 跳转至后台角色列表页面
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/gotoList")
	public ModelAndView gotoList() {
		return new ModelAndView("sys/role/list");
	}

	/**
	 * @dec 获取后台角色列表
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params) {
		return sysRoleService.queryList(params);
	}

	/**
	 * @dec 跳转至后台角色新增或修改页面
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/gotoInfo")
	public ModelAndView gotoInfo(String id) {
		ModelAndView mav = new ModelAndView("sys/role/info");

		if (StrUtil.isNotBlank(id)) {
			SysRole sysRole = sysRoleService.getById(id);
			if (sysRole == null) {
				throw new MyException("非法请求！");
			}

			if (StrUtil.equals(SysRole.ADMIN_CODE, sysRole.getCode())) {
				throw new MyException("超级管理员不可被修改！");
			}

			mav.addObject("info", sysRole);
		}
		return mav;
	}

	/**
	 * @dec 获取角色权限
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysRoleId
	 * @return
	 */
	@PostMapping("/getPermission")
	public R getPermission(String sysRoleId) {
		QueryWrapper<SysResource> wrapper = new QueryWrapper<>();
		wrapper.eq("status", SysResource.STATUS_NORMAL);
		wrapper.and(i -> i.eq("type", SysResource.TYPE_MENU));
		wrapper.and(i -> i.isNull("parent_id").or().eq("parent_id", ""));
		wrapper.select("id", "name");
		wrapper.orderByAsc("sort");
		wrapper.orderByDesc("create_time");
		List<SysResource> list = sysResourceService.list(wrapper);

		List<String> sysResourceIds = sysResourceService.getIdsBySysRoleId(sysRoleId);
		List<Map<String, Object>> permission = sysResourceService.getPermission(list, sysResourceIds);
		return R.ok(permission);
	}

	/**
	 * @dec 验证后台角色编码是否存在
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param code
	 * @param sysRoleId
	 * @return
	 */
	@PostMapping("/checkCode")
	public R checkCode(String code, String sysRoleId) {
		boolean checkOnly = sysRoleService.checkOnly(sysRoleId, "code", code.toUpperCase());
		return R.ok(!checkOnly);
	}

	/**
	 * @dec 添加后台角色
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysRole
	 * @return
	 */
	@PostMapping
	public R save(@Valid @RequestBody SysRole sysRole) {
		String code = sysRole.getCode().toUpperCase();
		boolean checkCode = sysRoleService.exist("code", SqlEmnus.EQ, code);
		if (checkCode) {
			return R.error("该角色编码已被使用！");
		}

		sysRole.setCode(code);
		return sysRoleService.add(sysRole);
	}

	/**
	 * @dec 修改后台角色
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysRole
	 * @return
	 */
	@PutMapping
	public R edit(@Valid @RequestBody SysRole sysRole) {
		String code = sysRole.getCode().toUpperCase();
		boolean checkCode = sysRoleService.checkOnly(sysRole.getId(), "id", code);
		if (!checkCode) {
			return R.error("该角色编码已被使用！");
		}
		if (StrUtil.equals(SysRole.ADMIN_CODE, code)) {
			return R.error("超级管理员不可被修改！");
		}

		sysRole.setCode(code);
		return sysRoleService.edit(sysRole);
	}

	/**
	 * @dec 删除后台角色
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
		List<SysRole> list = sysRoleService.list("id", SqlEmnus.IN, idList, "code");
		if (CollUtil.isEmpty(list)) {
			return R.error("非法请求！");
		}

		boolean isAdmin = list.stream().anyMatch(r -> StrUtil.equals(SysRole.ADMIN_CODE, r.getCode()));
		if (isAdmin) {
			return R.error("超级管理员不可被删除！");
		}

		return sysRoleService.del(idList);
	}

	/**
	 * @dec 启用或者禁用后台角色
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

		SysRole sysRole = sysRoleService.getById(id);
		if (sysRole == null) {
			return R.error("非法请求！");
		}
		if (StrUtil.equals(SysRole.ADMIN_CODE, sysRole.getCode())) {
			return R.error("超级管理员不可被修改！");
		}

		sysRole.setStatus(status);
		boolean usable = sysRoleService.updateById(sysRole);
		if (usable) {
			return R.ok();
		}

		return R.error();
	}

}
