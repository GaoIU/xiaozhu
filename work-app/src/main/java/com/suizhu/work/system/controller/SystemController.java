package com.suizhu.work.system.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.work.entity.SysConfig;
import com.suizhu.work.system.service.SysConfigService;
import com.suizhu.work.user.service.UserService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class SystemController {

	private final SysConfigService sysConfigService;

	private final UserService userService;

	/**
	 * @dec 获取版本信息
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param userAgent
	 * @return
	 */
	@GetMapping("/")
	public R getVersion(@RequestHeader(HttpHeaders.USER_AGENT) String userAgent) {
		QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("status", SysConfig.STATUS_NORMAL);
		userAgent = userAgent.toLowerCase();

		if (userAgent.contains("iphone") || userAgent.contains("ipad") || userAgent.contains("ipod")) {
			queryWrapper.and(i -> i.eq("name", "ios_version"));
		} else {
			queryWrapper.and(i -> i.eq("name", "android_version"));
		}
		queryWrapper.select("val");
		SysConfig sysConfig = sysConfigService.getOne(queryWrapper);

		return R.ok(JSONUtil.parseObj(sysConfig.getVal()));
	}

	/**
	 * @dec 账号密码登录
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param username
	 * @param password
	 * @param clientId
	 * @return
	 */
	@PostMapping("/signIn")
	public R signIn(String username, String password, @RequestHeader("Client-Id") String clientId) {
		if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
			throw new BadRequestException("账号或密码不能为空！");
		}

		return userService.signIn(username, password, clientId);
	}

	/**
	 * @dec Token登录
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param clientId
	 * @param token
	 * @return
	 */
	@PostMapping("/signToken")
	public R signToken(@RequestHeader("Client-Id") String clientId,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		return userService.signToken(clientId, token);
	}

	/**
	 * @dec 发送短信验证码
	 * @date Mar 2, 2019
	 * @author gaochao
	 * @param username
	 * @return
	 */
	@PostMapping("/forget")
	public R forget(String username) {
		if (!CommonUtil.checkMobile(username)) {
			throw new BadRequestException("手机号码不合法！");
		}

		return userService.sendSmsCode(username);
	}

	/**
	 * @dec 根据短信验证码重置密码
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @param username
	 * @param password
	 * @param code
	 * @return
	 */
	@PostMapping("/restPwd")
	public R restPwd(String username, String password, String code) {
		if (StrUtil.isBlank(code)) {
			throw new BadRequestException("验证码不能为空！");
		}

		if (StrUtil.isBlank(password)) {
			throw new BadRequestException("密码不能为空！");
		}

		if (password.length() < 6 || password.length() > 16) {
			throw new BadRequestException("密码长度只能在6或16位之间！");
		}

		if (StrUtil.isBlank(username)) {
			throw new BadRequestException("手机号码不能为空！");
		}

		return userService.restPwd(username, password, code);
	}

	/**
	 * @dec 分享
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param userAgent
	 * @return
	 */
	@GetMapping("/share")
	public R share(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
			@RequestHeader(HttpHeaders.USER_AGENT) String userAgent) {
		QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("status", SysConfig.STATUS_NORMAL);
		userAgent = userAgent.toLowerCase();

		if (userAgent.contains("iphone") || userAgent.contains("ipad") || userAgent.contains("ipod")) {
			queryWrapper.and(i -> i.eq("name", "ios_share"));
		} else {
			queryWrapper.and(i -> i.eq("name", "android_share"));
		}
		queryWrapper.select("val");
		SysConfig sysConfig = sysConfigService.getOne(queryWrapper);
		JSONObject parseObj = JSONUtil.parseObj(sysConfig.getVal());
		String title = parseObj.getStr("title");
		String url = parseObj.getStr("url");

		JSONObject json = CommonUtil.decodeToken(token);
		String realName = userService.getById(json.getStr("id")).getRealName();
		String dec = String.format(parseObj.getStr("dec"), realName);

		Map<String, String> data = new HashMap<>(3);
		data.put("title", title);
		data.put("dec", dec);
		data.put("url", url);
		return R.ok(data);
	}

}
