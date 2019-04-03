package com.suizhu.cms.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.cms.entity.SysRole;
import com.suizhu.cms.entity.SysRoleResource;
import com.suizhu.cms.entity.SysUserRole;
import com.suizhu.cms.mapper.SysRoleMapper;
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
 * 后台角色表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@Service
@AllArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

	private final SysUserRoleService sysUserRoleService;

	private final SysRoleResourceService sysRoleResourceService;

	/**
	 * @dec 根据后台用户ID查询
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysUserId
	 * @return
	 */
	@Override
	public List<SysRole> findBySysUserId(String sysUserId) {
		List<SysUserRole> list = sysUserRoleService.list("sys_user_id", SqlEmnus.EQ, sysUserId, "sys_role_id");
		if (CollUtil.isEmpty(list)) {
			return null;
		}

		List<String> sysRoleIds = list.stream().map(SysUserRole::getSysRoleId).collect(Collectors.toList());
		QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("status", SysRole.STATUS_NORMAL);
		queryWrapper.and(i -> i.in("id", sysRoleIds));
		queryWrapper.select("id", "name", "code");
		return list(queryWrapper);
	}

	/**
	 * @dec 获取后台角色列表
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
		String beginTime = MapUtils.getString(params, "beginTime");
		String endTime = MapUtils.getString(params, "endTime");
		QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("create_time");

		if (StrUtil.isNotBlank(name)) {
			queryWrapper.and(i -> i.like("name", name));
		}
		if (StrUtil.isNotBlank(code)) {
			queryWrapper.and(i -> i.like("code", code));
		}
		if (status != null) {
			queryWrapper.and(i -> i.eq("status", status));
		}
		if (StrUtil.isNotBlank(beginTime)) {
			queryWrapper.and(i -> i.ge("create_time", beginTime));
		}
		if (StrUtil.isNotBlank(endTime)) {
			queryWrapper.and(i -> i.le("create_time", endTime + " 23:59:59"));
		}

		IPage<SysRole> page = new Page<>(current, size);
		page = page(page, queryWrapper);
		return R.ok(page);
	}

	/**
	 * @dec 新增后台角色
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysRole
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R add(SysRole sysRole) {
		boolean save = save(sysRole);
		if (save) {
			if (StrUtil.isNotBlank(sysRole.getSysResourceIds())) {
				String[] sysResourceIds = sysRole.getSysResourceIds().split(",");
				for (String sysResourceId : sysResourceIds) {
					SysRoleResource sysRoleResource = new SysRoleResource(sysRole.getId(), sysResourceId.trim());
					sysRoleResourceService.save(sysRoleResource);
				}
			}

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改后台角色
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysRole
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R edit(SysRole sysRole) {
		boolean update = updateById(sysRole);
		if (update) {
			List<SysRoleResource> list = sysRoleResourceService.list("sys_role_id", SqlEmnus.EQ, sysRole.getId(), "id");
			if (CollUtil.isNotEmpty(list)) {
				List<String> ids = list.stream().map(SysRoleResource::getId).collect(Collectors.toList());
				sysRoleResourceService.removeByIds(ids);
			}

			if (StrUtil.isNotBlank(sysRole.getSysResourceIds())) {
				String[] sysResourceIds = sysRole.getSysResourceIds().split(",");
				for (String sysResourceId : sysResourceIds) {
					SysRoleResource sysRoleResource = new SysRoleResource(sysRole.getId(), sysResourceId.trim());
					sysRoleResourceService.save(sysRoleResource);
				}
			}

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 删除后台角色
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param idList
	 * @return
	 */
	@Override
	public R del(List<String> idList) {
		boolean remove = removeByIds(idList);
		if (remove) {
			List<SysRoleResource> list = sysRoleResourceService.list("sys_role_id", SqlEmnus.IN, idList, "id");
			if (CollUtil.isNotEmpty(list)) {
				List<String> ids = list.stream().map(SysRoleResource::getId).collect(Collectors.toList());
				sysRoleResourceService.removeByIds(ids);
			}

			List<SysUserRole> list2 = sysUserRoleService.list("sys_role_id", SqlEmnus.IN, idList, "id");
			if (CollUtil.isNotEmpty(list2)) {
				List<String> ids = list2.stream().map(SysUserRole::getId).collect(Collectors.toList());
				sysUserRoleService.removeByIds(ids);
			}

			return R.ok();
		}

		return R.error();
	}

}
