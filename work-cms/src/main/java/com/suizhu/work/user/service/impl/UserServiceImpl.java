package com.suizhu.work.user.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.cms.config.MyContansConfig;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.core.RedisClient;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.common.util.EncrypUtil;
import com.suizhu.common.util.RsaUtil;
import com.suizhu.work.build.service.BuildTipService;
import com.suizhu.work.entity.BuildTip;
import com.suizhu.work.entity.Organization;
import com.suizhu.work.entity.User;
import com.suizhu.work.entity.UserOrg;
import com.suizhu.work.user.mapper.UserMapper;
import com.suizhu.work.user.service.OrganizationService;
import com.suizhu.work.user.service.UserOrgService;
import com.suizhu.work.user.service.UserService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-28
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

	private final MyContansConfig myContansConfig;

	private final RedisClient redisClient;

	private final UserOrgService userOrgService;

	private final BuildTipService buildTipService;

	private final OrganizationService organizationService;

	/**
	 * @dec 获取用户列表
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public R queryList(Map<String, Object> params) {
		Integer current = MapUtils.getInteger(params, "current", 1);
		Integer size = MapUtils.getInteger(params, "size", 15);
		String username = MapUtils.getString(params, "username");
		Integer status = MapUtils.getInteger(params, "status");
		Integer type = MapUtils.getInteger(params, "type");
		String beginTime = MapUtils.getString(params, "beginTime");
		String endTime = MapUtils.getString(params, "endTime");
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("create_time");

		if (StrUtil.isNotBlank(username)) {
			queryWrapper.and(i -> i.like("username", username));
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

		IPage<User> page = new Page<>(current, size);
		page = page(page, queryWrapper);

		IPage<Map<String, Object>> result = new Page<>(current, size);
		result.setTotal(page.getTotal());
		result.setPages(page.getPages());

		List<User> records = page.getRecords();
		if (CollUtil.isEmpty(records)) {
			result.setRecords(new ArrayList<>(0));
		} else {
			List<Map<String, Object>> list = records.stream().map(u -> {
				Map<String, Object> data = new HashMap<>(7);
				data.put("id", u.getId());
				data.put("username", u.getUsername());
				data.put("realName", u.getRealName());
				data.put("email", u.getEmail());
				data.put("status", u.getStatus());
				data.put("createTime", u.getCreateTime());

				UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, u.getId(), "org_id");
				if (userOrg == null) {
					data.put("dection", "");
				} else {
					Organization organization = organizationService.getOne("id", SqlEmnus.EQ, userOrg.getOrgId(),
							"name");
					data.put("dection", organization.getName());
				}
				return data;
			}).collect(Collectors.toList());

			result.setRecords(list);
		}

		return R.ok(result);
	}

	/**
	 * @dec 新增用户
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param user
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R addUser(User user) {
		user.setPassword(EncrypUtil.encode(User.DEFAULT_PASSWORD));
		user.setAvatar(myContansConfig.getWorkAvatar());
		boolean save = save(user);
		if (save) {
			UserOrg userOrg = new UserOrg(user.getRealName(), user.getUsername(), user.getEmail(), user.getId(),
					user.getOrgId());
			userOrgService.save(userOrg);

			QueryWrapper<BuildTip> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("org_id", user.getOrgId()).select("build_enginer_id");
			List<BuildTip> list = buildTipService.list(queryWrapper);
			if (CollUtil.isNotEmpty(list)) {
				list.stream().map(BuildTip::getBuildEnginerId).collect(Collectors.toList()).forEach(beid -> {
					BuildTip buildTip = new BuildTip(beid, user.getId(), user.getOrgId());
					buildTipService.save(buildTip);
				});
			}

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 禁用或启用用户
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param id
	 * @param status
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R usable(String id, Integer status) {
		User user = getById(id);
		if (user == null) {
			return R.error("非法请求！");
		}

		user.setStatus(status);
		boolean update = updateById(user);
		if (update) {
			Object token = redisClient.get(id);
			if (token != null) {
				JSONObject parseObj = JSONUtil.parseObj(RsaUtil.decode(token.toString()));
				parseObj.replace("status", status);
				token = RsaUtil.encoder(JSONUtil.toJsonStr(parseObj));

				Long expire = redisClient.getExpire(id);
				redisClient.set(id, token, expire);
			}

			return R.ok();
		}

		return R.error();
	}

}
