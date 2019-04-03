package com.suizhu.work.user.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.common.util.EncrypUtil;
import com.suizhu.common.util.SpringContextHolder;
import com.suizhu.work.build.service.BuildEnginerService;
import com.suizhu.work.build.service.BuildTipService;
import com.suizhu.work.config.MyContansConfig;
import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildTip;
import com.suizhu.work.entity.Doorway;
import com.suizhu.work.entity.LogSms;
import com.suizhu.work.entity.LogUserOrg;
import com.suizhu.work.entity.User;
import com.suizhu.work.entity.UserOrg;
import com.suizhu.work.log.service.LogUserOrgService;
import com.suizhu.work.user.mapper.UserOrgMapper;
import com.suizhu.work.user.service.UserOrgService;
import com.suizhu.work.user.service.UserService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 公司人员表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-13
 */
@Service
@AllArgsConstructor
public class UserOrgServiceImpl extends ServiceImpl<UserOrgMapper, UserOrg> implements UserOrgService {

	private final UserService userService;

	private final MyContansConfig myContansConfig;

	private final LogUserOrgService logUserOrgService;

	private final BuildTipService buildTipService;

	/**
	 * @dec 新增公司人员
	 * @date Mar 13, 2019
	 * @author gaochao
	 * @param list
	 * @param userOrg
	 * @param json
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R add(List<UserOrg> list, UserOrg userOrg, JSONObject json) {
		int error = 0;
		for (UserOrg uo : list) {
			String name = uo.getName();
			if (StrUtil.isBlank(name)) {
				throw new BadRequestException("姓名不能为空！");
			}
			if (name.length() > 12) {
				throw new BadRequestException("姓名长度不能超过12位！");
			}
			String dep = uo.getDep();
			if (StrUtil.isBlank(dep)) {
				throw new BadRequestException("部门不能为空！");
			}
			if (dep.length() > 8) {
				throw new BadRequestException("部门长度不能超过8位！");
			}
			String pos = uo.getPos();
			if (StrUtil.isNotBlank(pos)) {
				if (pos.length() > 8) {
					throw new BadRequestException("职位长度不能超过8位！");
				}
			}
			String workKind = uo.getWorkKind();
			if (StrUtil.isNotBlank(workKind)) {
				if (workKind.length() > 8) {
					throw new BadRequestException("工种长度不能超过8位！");
				}
			}
			String workNum = uo.getWorkNum();
			if (StrUtil.isNotBlank(workNum)) {
				if (workNum.length() > 16) {
					throw new BadRequestException("工号长度不能超过16位！");
				}
			}
			Integer workOut = uo.getWorkOut();
			if (workOut == null) {
				throw new BadRequestException();
			}

			String mobile = uo.getMobile();
			if (StrUtil.isBlank(mobile)) {
				throw new BadRequestException("手机号码不能为空！");
			}
			if (!CommonUtil.checkMobile(mobile)) {
				throw new BadRequestException("存在不合法的手机号码！");
			}

			String email = uo.getEmail();
			if (StrUtil.isNotBlank(email)) {
				if (!CommonUtil.checkEmail(email)) {
					throw new BadRequestException("存在不合法的邮箱！");
				}

				QueryWrapper<UserOrg> emailQuery = new QueryWrapper<>();
				emailQuery.eq("org_id", userOrg.getOrgId()).and(i -> i.eq("email", email));
				boolean exist = exist(emailQuery);
				if (exist) {
					throw new BadRequestException("此邮箱：" + email + "已被使用！");
				}
			}

			QueryWrapper<UserOrg> mobileQuery = new QueryWrapper<>();
			mobileQuery.eq("org_id", userOrg.getOrgId()).and(i -> i.eq("mobile", mobile));
			boolean exist = exist(mobileQuery);
			if (exist) {
				throw new BadRequestException("此手机号码：" + mobile + "已被使用！");
			}

			uo.setOrgId(userOrg.getOrgId());
			User user = userService.getOne("username", SqlEmnus.EQ, mobile, "id");
			if (user == null) {
				String password = EncrypUtil.encode(User.DEFAULT_PASSWORD);
				user = new User(mobile, password, uo.getName(), myContansConfig.getWorkAvatar(), email,
						json.getStr("id"));
				userService.save(user);

				String content = String.format(myContansConfig.getSmsContentInvite(), json.getStr("username"), mobile,
						User.DEFAULT_PASSWORD);
				userService.sendSms(LogSms.TYPE_INVITE, mobile, content);
			}
			uo.setUserId(user.getId());

			boolean save = save(uo);
			if (save) {
				DoorwayService doorwayService = SpringContextHolder.getBean("doorwayServiceImpl", DoorwayService.class);
				List<Doorway> doorways = doorwayService.list("org_id", SqlEmnus.EQ, userOrg.getOrgId(), "id");
				if (CollUtil.isNotEmpty(doorways)) {
					List<String> dids = doorways.stream().map(Doorway::getId).collect(Collectors.toList());
					BuildEnginerService buildEnginerService = SpringContextHolder.getBean("buildEnginerServiceImpl",
							BuildEnginerService.class);
					List<String> beids = buildEnginerService.list("doorway_id", SqlEmnus.IN, dids, "id").stream()
							.map(BuildEnginer::getId).collect(Collectors.toList());

					if (CollUtil.isNotEmpty(beids)) {
						beids.forEach(beid -> {
							BuildTip buildTip = new BuildTip(beid, uo.getUserId(), uo.getOrgId());
							buildTipService.save(buildTip);
						});
					}
				}
			} else {
				error++;
			}
		}

		if (error == 0) {
			LogUserOrg logUserOrg = new LogUserOrg(json.getStr("id"), LogUserOrg.TYPE_ADD);
			logUserOrgService.save(logUserOrg);

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改公司人员
	 * @date Mar 13, 2019
	 * @author gaochao
	 * @param userOrg
	 * @param json
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R edit(UserOrg userOrg, JSONObject json) {
		String mobile = userOrg.getMobile();
		QueryWrapper<UserOrg> mobileQuery = new QueryWrapper<>();
		mobileQuery.eq("org_id", userOrg.getOrgId()).and(i -> i.eq("mobile", mobile));
		boolean mobleExist = exist(mobileQuery);
		if (mobleExist) {
			UserOrg oldUo = getOne("id", SqlEmnus.EQ, userOrg.getId(), "mobile");
			if (oldUo != null && !StrUtil.equals(mobile, oldUo.getMobile())) {
				throw new BadRequestException("该手机号码已存在！");
			}
		}

		String email = userOrg.getEmail();
		if (StrUtil.isNotBlank(email)) {
			if (!CommonUtil.checkEmail(email)) {
				throw new BadRequestException("该邮箱不合法！");
			}

			QueryWrapper<UserOrg> emailQuery = new QueryWrapper<>();
			emailQuery.eq("org_id", userOrg.getOrgId()).and(i -> i.eq("email", email));
			boolean exist = exist(emailQuery);
			if (exist) {
				UserOrg oldUo = getOne("id", SqlEmnus.EQ, userOrg.getId(), "email");
				if (oldUo != null && !StrUtil.equals(email, oldUo.getEmail())) {
					throw new BadRequestException("该邮箱已存在！");
				}
			}
		}

		User user = userService.getOne("username", SqlEmnus.EQ, mobile, "id");
		if (user == null) {
			String password = EncrypUtil.encode(User.DEFAULT_PASSWORD);
			user = new User(mobile, password, userOrg.getName(), myContansConfig.getWorkAvatar(), email,
					json.getStr("id"));
			userService.save(user);

			String content = String.format(myContansConfig.getSmsContentInvite(), json.getStr("username"), mobile,
					User.DEFAULT_PASSWORD);
			userService.sendSms(LogSms.TYPE_INVITE, mobile, content);
		}
		userOrg.setUserId(user.getId());

		boolean update = updateById(userOrg);
		if (update) {
			LogUserOrg logUserOrg = new LogUserOrg(json.getStr("id"), LogUserOrg.TYPE_EDIT);
			logUserOrgService.save(logUserOrg);

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 删除公司人员
	 * @date Mar 13, 2019
	 * @author gaochao
	 * @param id
	 * @param json
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R del(String id, JSONObject json) {
		UserOrg userOrg = getOne("id", SqlEmnus.EQ, id, "org_id", "user_id");
		boolean remove = removeById(id);
		if (remove) {
			QueryWrapper<BuildTip> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("user_id", userOrg.getUserId()).and(i -> i.eq("org_id", userOrg.getOrgId()));
			buildTipService.remove(queryWrapper);

			LogUserOrg logUserOrg = new LogUserOrg(json.getStr("id"), LogUserOrg.TYPE_DEL);
			logUserOrgService.save(logUserOrg);

			return R.ok();
		}

		return R.error();
	}

}
