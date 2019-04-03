package com.suizhu.work.user.service.impl;

import java.util.List;

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
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildTip;
import com.suizhu.work.entity.LogSms;
import com.suizhu.work.entity.User;
import com.suizhu.work.entity.UserDoorway;
import com.suizhu.work.user.mapper.UserDoorwayMapper;
import com.suizhu.work.user.service.UserDoorwayService;
import com.suizhu.work.user.service.UserService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 用户表 - 门店表 中间关联表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-11
 */
@Service
@AllArgsConstructor
public class UserDoorwayServiceImpl extends ServiceImpl<UserDoorwayMapper, UserDoorway> implements UserDoorwayService {

	private final UserService userService;

	private final MyContansConfig myContansConfig;

	private final BuildTipService buildTipService;

	/**
	 * @dec 新增人员
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param list
	 * @param doorwayId
	 * @param jo
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R add(List<UserDoorway> list, String doorwayId, JSONObject json) {
		int error = 0;
		for (UserDoorway ud : list) {
			String name = ud.getName();
			if (StrUtil.isBlank(name)) {
				throw new BadRequestException("姓名不能为空！");
			}
			if (name.length() > 12) {
				throw new BadRequestException("姓名长度不能超过12位！");
			}
			String dep = ud.getDep();
			if (StrUtil.isBlank(dep)) {
				throw new BadRequestException("部门不能为空！");
			}
			if (dep.length() > 8) {
				throw new BadRequestException("部门长度不能超过8位！");
			}
			String pos = ud.getPos();
			if (StrUtil.isNotBlank(pos)) {
				if (pos.length() > 8) {
					throw new BadRequestException("职位长度不能超过8位！");
				}
			}
			String workKind = ud.getWorkKind();
			if (StrUtil.isNotBlank(workKind)) {
				if (workKind.length() > 8) {
					throw new BadRequestException("工种长度不能超过8位！");
				}
			}
			String workNum = ud.getWorkNum();
			if (StrUtil.isNotBlank(workNum)) {
				if (workNum.length() > 16) {
					throw new BadRequestException("工号长度不能超过16位！");
				}
			}
			Integer workOut = ud.getWorkOut();
			if (workOut == null) {
				throw new BadRequestException();
			}

			String mobile = ud.getMobile();
			if (StrUtil.isBlank(mobile)) {
				throw new BadRequestException("手机号码不能为空！");
			}
			if (!CommonUtil.checkMobile(mobile)) {
				throw new BadRequestException("存在不合法的手机号码！");
			}

			String email = ud.getEmail();
			if (StrUtil.isNotBlank(email)) {
				if (!CommonUtil.checkEmail(email)) {
					throw new BadRequestException("存在不合法的邮箱！");
				}

				QueryWrapper<UserDoorway> emailQuery = new QueryWrapper<>();
				emailQuery.eq("doorway_id", doorwayId).and(i -> i.eq("email", email));
				boolean exist = exist(emailQuery);
				if (exist) {
					throw new BadRequestException("此邮箱：" + email + "已被使用！");
				}
			}

			QueryWrapper<UserDoorway> mobileQuery = new QueryWrapper<>();
			mobileQuery.eq("doorway_id", doorwayId).and(i -> i.eq("mobile", mobile));
			boolean exist = exist(mobileQuery);
			if (exist) {
				throw new BadRequestException("此手机号码：" + mobile + "已被使用！");
			}

			ud.setDoorwayId(doorwayId);
			User user = userService.getOne("username", SqlEmnus.EQ, mobile, "id");
			if (user == null) {
				String password = EncrypUtil.encode(User.DEFAULT_PASSWORD);
				user = new User(mobile, password, ud.getName(), myContansConfig.getWorkAvatar(), email,
						json.getStr("id"));
				userService.save(user);

				String content = String.format(myContansConfig.getSmsContentInvite(), json.getStr("username"), mobile,
						User.DEFAULT_PASSWORD);
				userService.sendSms(LogSms.TYPE_INVITE, mobile, content);
			}
			ud.setUserId(user.getId());
			ud.setType(UserDoorway.TYPE_NORMAL);

			boolean save = save(ud);
			if (!save) {
				error++;
			} else {
				BuildEnginerService buildEnginerService = SpringContextHolder.getBean("buildEnginerServiceImpl",
						BuildEnginerService.class);
				BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, doorwayId, "id");
				BuildTip buildTip = new BuildTip(buildEnginer.getId(), user.getId(), null);
				buildTipService.save(buildTip);
			}
		}

		if (error == 0) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改人员
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param userDoorway
	 * @param jo
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R edit(UserDoorway userDoorway, JSONObject json) {
		User user = userService.getOne("username", SqlEmnus.EQ, userDoorway.getMobile(), "id");
		if (user == null) {
			user = new User();
			user.setUsername(userDoorway.getName());
			user.setRealName(userDoorway.getMobile());
			user.setAvatar(myContansConfig.getWorkAvatar());
			user.setPassword(EncrypUtil.encode(User.DEFAULT_PASSWORD));
			user.setInviteId(json.getStr("id"));
			userService.save(user);

			String content = String.format(myContansConfig.getSmsContentInvite(), json.getStr("username"),
					userDoorway.getMobile(), User.DEFAULT_PASSWORD);
			userService.sendSms(LogSms.TYPE_INVITE, userDoorway.getMobile(), content);
		}

		userDoorway.setUserId(user.getId());
		boolean update = updateById(userDoorway);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

}
