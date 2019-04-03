package com.suizhu.cms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.cms.config.MyContansConfig;
import com.suizhu.cms.entity.SysUser;
import com.suizhu.cms.entity.SysUserRole;
import com.suizhu.cms.mapper.SysUserMapper;
import com.suizhu.cms.service.SysUserRoleService;
import com.suizhu.cms.service.SysUserService;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.FdfsClient;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.common.util.EncrypUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@Service
@AllArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

	private final FdfsClient fdfsClient;

	private final MyContansConfig myContansConfig;

	private final SysUserRoleService sysUserRoleService;

	/**
	 * @dec 上传后台用户头像
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param avatar
	 * @param sysUserId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R uploadAvatar(MultipartFile avatar, String sysUserId) {
		SysUser sysUser = getById(sysUserId);
		String oldAvatar = sysUser.getAvatar();
		String fileId = oldAvatar.replaceAll(myContansConfig.getFdfsServer(), "");

		String newAvatar;
		if (StrUtil.equals(oldAvatar, myContansConfig.getCmsAvatar())) {
			newAvatar = fdfsClient.upload(avatar);
		} else {
			newAvatar = fdfsClient.updateFile(avatar, fileId);
		}

		sysUser.setAvatar(newAvatar);
		boolean update = updateById(sysUser);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 获取后台用户列表
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public R queryList(Map<String, Object> params) {
		Integer current = MapUtils.getInteger(params, "current", 1);
		Integer size = MapUtils.getInteger(params, "size", 15);
		String userName = MapUtils.getString(params, "userName");
		String mobile = MapUtils.getString(params, "phone");
		Integer status = MapUtils.getInteger(params, "status");
		String beginTime = MapUtils.getString(params, "beginTime");
		String endTime = MapUtils.getString(params, "endTime");
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("create_time");

		if (StrUtil.isNotBlank(userName)) {
			queryWrapper.and(i -> i.like("username", userName));
		}
		if (StrUtil.isNotBlank(mobile)) {
			queryWrapper.and(i -> i.like("mobile", mobile));
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

		IPage<SysUser> page = new Page<>(current, size);
		page = page(page, queryWrapper);
		return R.ok(page);
	}

	/**
	 * @dec 添加后台用户
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysUser
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R add(SysUser sysUser) {
		String password = EncrypUtil.encode(SysUser.DEFAULT_PASSWORD);
		sysUser.setPassword(password);
		sysUser.setAvatar(myContansConfig.getCmsAvatar());
		boolean save = save(sysUser);
		if (save) {
			if (StrUtil.isNotBlank(sysUser.getSysRoleIds())) {
				String[] sysRoleIds = sysUser.getSysRoleIds().split(",");
				if (ArrayUtil.isNotEmpty(sysRoleIds)) {
					for (String sysRoleId : sysRoleIds) {
						SysUserRole sysUserRole = new SysUserRole(sysUser.getId(), sysRoleId);
						sysUserRoleService.save(sysUserRole);
					}
				}
			}

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改后台用户
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysUser
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R edit(@Valid SysUser sysUser) {
		boolean update = updateById(sysUser);
		if (update) {
			List<SysUserRole> list = sysUserRoleService.list("sys_user_id", SqlEmnus.EQ, sysUser.getId(), "id");
			if (CollUtil.isNotEmpty(list)) {
				List<String> ids = list.stream().map(SysUserRole::getId).collect(Collectors.toList());
				sysUserRoleService.removeByIds(ids);
			}

			if (StrUtil.isNotBlank(sysUser.getSysRoleIds())) {
				String[] sysRoleIds = sysUser.getSysRoleIds().split(",");
				if (ArrayUtil.isNotEmpty(sysRoleIds)) {
					for (String sysRoleId : sysRoleIds) {
						SysUserRole sysUserRole = new SysUserRole(sysUser.getId(), sysRoleId);
						sysUserRoleService.save(sysUserRole);
					}
				}
			}

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 删除后台用户
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param idList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R del(ArrayList<String> idList) {
		boolean remove = removeByIds(idList);
		if (remove) {
			List<SysUserRole> list = sysUserRoleService.list("sys_user_id", SqlEmnus.IN, idList, "id");
			if (CollUtil.isNotEmpty(list)) {
				List<String> ids = list.stream().map(SysUserRole::getId).collect(Collectors.toList());
				sysUserRoleService.removeByIds(ids);
			}

			return R.ok();
		}

		return R.error();
	}

}
