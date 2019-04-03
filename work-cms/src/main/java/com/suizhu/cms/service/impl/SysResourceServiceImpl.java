package com.suizhu.cms.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.cms.entity.SysResource;
import com.suizhu.cms.entity.SysRole;
import com.suizhu.cms.entity.SysRoleResource;
import com.suizhu.cms.entity.SysUserRole;
import com.suizhu.cms.mapper.SysResourceMapper;
import com.suizhu.cms.service.SysResourceService;
import com.suizhu.cms.service.SysRoleResourceService;
import com.suizhu.cms.service.SysRoleService;
import com.suizhu.cms.service.SysUserRoleService;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 后台资源表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@Service
@AllArgsConstructor
public class SysResourceServiceImpl extends ServiceImpl<SysResourceMapper, SysResource> implements SysResourceService {

	private final SysUserRoleService sysUserRoleService;

	private final SysRoleService sysRoleService;

	private final SysRoleResourceService sysRoleResourceService;

	/**
	 * @dec 根据后台用户ID和动态条件查询
	 * @date Feb 20, 2019
	 * @author gaochao
	 * @param sysUserId
	 * @param wrapper
	 * @return
	 */
	@Override
	public List<SysResource> findByWrapper(String sysUserId, QueryWrapper<SysResource> wrapper) {
		List<SysUserRole> list = sysUserRoleService.list("sys_user_id", SqlEmnus.EQ, sysUserId, "sys_role_id");
		if (CollUtil.isEmpty(list)) {
			return null;
		}

		List<String> sysRoleIds = list.stream().map(SysUserRole::getSysRoleId).collect(Collectors.toList());
		QueryWrapper<SysRole> sysRoleWrapper = new QueryWrapper<>();
		sysRoleWrapper.eq("status", SysRole.STATUS_NORMAL);
		sysRoleWrapper.and(i -> i.in("id", sysRoleIds));
		sysRoleWrapper.select("code");
		boolean isAdmin = sysRoleService.list(sysRoleWrapper).stream().map(SysRole::getCode)
				.collect(Collectors.toList()).stream().anyMatch(code -> StrUtil.equals(SysRole.ADMIN_CODE, code));

		if (isAdmin) {
			return list(wrapper);
		}

		List<SysRoleResource> srrs = sysRoleResourceService.list("sys_role_id", SqlEmnus.IN, sysRoleIds,
				"sys_resource_id");
		if (CollUtil.isEmpty(srrs)) {
			return null;
		}

		List<String> sysResourceIds = srrs.stream().map(SysRoleResource::getSysResourceId).collect(Collectors.toList());
		wrapper.and(i -> i.in("id", sysResourceIds));
		return list(wrapper);
	}

	/**
	 * @dec 获取资源树
	 * @date Feb 20, 2019
	 * @author gaochao
	 * @param list
	 * @param childName
	 * @param conditionType
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getMenu(List<SysResource> list, String childName, Integer conditionType) {
		List<Map<String, Object>> tree = list.stream().map(sr -> {
			Map<String, Object> menu = new LinkedHashMap<>(5);
			menu.put("id", sr.getId());
			menu.put("name", sr.getName());
			menu.put("url", sr.getUrl());
			menu.put("icon", sr.getIcon());
			menu.put("type", sr.getType());

			QueryWrapper<SysResource> wrapper = new QueryWrapper<>();
			wrapper.eq("status", SysResource.STATUS_NORMAL);
			wrapper.and(i -> i.eq("parent_id", sr.getId()));
			if (conditionType != null) {
				wrapper.and(i -> i.eq("type", conditionType));
			}

			wrapper.select("id", "name", "url", "icon", "type");
			wrapper.orderByAsc("sort");
			wrapper.orderByDesc("create_time");
			menu.put(childName, getMenu(list(wrapper), childName, conditionType));
			return menu;
		}).collect(Collectors.toList());

		return tree;
	}

	/**
	 * @dec 获取后台资源列表
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public R queryList(Map<String, Object> params) {
		Integer current = MapUtils.getInteger(params, "current", 1);
		Integer size = MapUtils.getInteger(params, "size", 15);
		String name = MapUtils.getString(params, "name");
		String code = MapUtils.getString(params, "code");
		Integer status = MapUtils.getInteger(params, "status");
		Integer type = MapUtils.getInteger(params, "type");
		String beginTime = MapUtils.getString(params, "beginTime");
		String endTime = MapUtils.getString(params, "endTime");
		QueryWrapper<SysResource> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("create_time");

		if (StrUtil.isNotBlank(name)) {
			queryWrapper.and(i -> i.like("name", name));
		}
		if (StrUtil.isNotBlank(code)) {
			queryWrapper.and(i -> i.like("code", code.toUpperCase()));
		}
		if (status != null) {
			queryWrapper.and(i -> i.eq("status", status));
		}
		if (type != null) {
			queryWrapper.and(i -> i.eq("type", type));
		}
		if (StrUtil.isNotBlank(beginTime)) {
			queryWrapper.and(i -> i.ge("create_time", beginTime));
		}
		if (StrUtil.isNotBlank(endTime)) {
			queryWrapper.and(i -> i.le("create_time", endTime + " 23:59:59"));
		}

		IPage<SysResource> page = new Page<>(current, size);
		page = page(page, queryWrapper);
		return R.ok(page);
	}

	/**
	 * @dec 根据ID查询信息
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@Override
	public Map<String, Object> view(String id) {
		return baseMapper.view(id);
	}

	/**
	 * @dec 根据后台角色ID获取ID结果集
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysRoleId
	 * @return
	 */
	@Override
	public List<String> getIdsBySysRoleId(String sysRoleId) {
		List<SysRoleResource> srrs = sysRoleResourceService.list("sys_role_id", SqlEmnus.EQ, sysRoleId,
				"sys_resource_id");
		if (CollUtil.isEmpty(srrs)) {
			return new ArrayList<>(0);
		}

		return srrs.stream().map(SysRoleResource::getSysResourceId).collect(Collectors.toList());
	}

	/**
	 * @dec 获取角色权限树菜单
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param list
	 * @param ids
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getPermission(List<SysResource> list, List<String> ids) {
		List<Map<String, Object>> collect = list.stream().map(r -> {
			Map<String, Object> data = new LinkedHashMap<>(4);
			data.put("id", r.getId());
			data.put("name", r.getName());

			boolean checked = false;
			if (CollUtil.isNotEmpty(ids)) {
				checked = ids.stream().anyMatch(id -> StrUtil.equals(r.getId(), id));
			}
			data.put("checked", checked);
			data.put("list", menuChild(r.getId(), ids));

			return data;
		}).collect(Collectors.toList());

		return collect;
	}

	/**
	 * @dec 获取角色权限子集
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param id
	 * @param ids
	 * @return
	 */
	private List<?> menuChild(String id, List<String> ids) {
		List<SysResource> list = getPermissionByParentId(id);
		if (CollUtil.isNotEmpty(list)) {
			List<Map<String, Object>> collect = list.stream().map(r -> {
				Map<String, Object> data = new LinkedHashMap<>(4);
				data.put("id", r.getId());
				data.put("name", r.getName());

				boolean checked = false;
				if (CollUtil.isNotEmpty(ids)) {
					checked = ids.stream().anyMatch(i -> StrUtil.equals(r.getId(), i));
				}
				data.put("checked", checked);
				data.put("list", getPermission(getPermissionByParentId(r.getId()), ids));

				return data;
			}).collect(Collectors.toList());

			return collect;
		}

		return list;
	}

	/**
	 * @dec 根据父级ID获取角色权限
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	private List<SysResource> getPermissionByParentId(String id) {
		QueryWrapper<SysResource> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("create_time");
		queryWrapper.orderByAsc("sort");
		queryWrapper.eq("status", SysResource.STATUS_NORMAL);
		queryWrapper.and(i -> i.eq("parent_id", id));
		queryWrapper.and(i -> i.ne("type", SysResource.TYPE_FUNCTION));
		queryWrapper.select("id", "name");
		return list(queryWrapper);
	}

}
