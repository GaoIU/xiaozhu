package com.suizhu.work.user.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.FdfsClient;
import com.suizhu.common.core.R;
import com.suizhu.common.core.RedisClient;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.common.exception.UnauthorizedException;
import com.suizhu.common.util.EncrypUtil;
import com.suizhu.common.util.RsaUtil;
import com.suizhu.work.config.MyContansConfig;
import com.suizhu.work.entity.LogSms;
import com.suizhu.work.entity.User;
import com.suizhu.work.system.service.LogSmsService;
import com.suizhu.work.user.mapper.UserMapper;
import com.suizhu.work.user.service.UserService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
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

	private final RedisClient redisClient;

	private final MyContansConfig myContansConfig;

	private final LogSmsService logSmsService;

	private final FdfsClient fdfsClient;

	/**
	 * @dec 账号密码登录
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param username
	 * @param password
	 * @param clientId
	 * @return
	 */
	@Override
	public R signIn(String username, String password, String clientId) {
		User user = getOne("username", SqlEmnus.EQ, username);
		if (user == null || !EncrypUtil.matches(password, user.getPassword())) {
			return R.error("账号或密码不匹配！");
		}

		if (User.STATUS_DISABLE == user.getStatus()) {
			return R.error("该账号已被禁用，请联系客服！");
		}

		Map<String, Object> data = new HashMap<>(5);
		data.put("id", user.getId());
		data.put("username", user.getUsername());
		data.put("email", user.getEmail());
		data.put("status", user.getStatus());
		String token = RsaUtil.encoder(JSONUtil.toJsonStr(data));
		data.put("token", DigestUtil.md5Hex(token));
		String redisToken = RsaUtil.encoder(JSONUtil.toJsonStr(data));
		redisClient.set(clientId, user.getId(), myContansConfig.getTokenExpireTime());
		redisClient.set(user.getId(), redisToken, myContansConfig.getTokenExpireTime());

		return R.ok(token);
	}

	/**
	 * @dec Token登录
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param clientId
	 * @param token
	 * @return
	 */
	@Override
	public R signToken(String clientId, String token) {
		Object userId = redisClient.get(clientId);
		if (userId == null) {
			throw new UnauthorizedException("登录信息已过期！");
		}

		Object redisToken = redisClient.get(userId.toString());
		if (redisToken == null) {
			throw new UnauthorizedException("登录信息已过期！");
		}

		JSONObject parseObj = JSONUtil.parseObj(RsaUtil.decode(redisToken.toString()));
		if (!StrUtil.equals(DigestUtil.md5Hex(token), parseObj.getStr("token"))) {
			throw new UnauthorizedException("该账号已在别处登录，请重新登录！");
		}

		User user = getById(userId.toString());
		if (User.STATUS_DISABLE == user.getStatus()) {
			return R.error("该账号已被禁用，请联系客服！");
		}

		parseObj.remove("token");
		parseObj.replace("username", user.getUsername());
		parseObj.replace("email", user.getEmail());
		parseObj.replace("status", user.getStatus());
		token = RsaUtil.encoder(JSONUtil.toJsonStr(parseObj));
		parseObj.put("token", DigestUtil.md5Hex(token));
		redisToken = RsaUtil.encoder(JSONUtil.toJsonStr(parseObj));
		redisClient.set(clientId, user.getId(), myContansConfig.getTokenExpireTime());
		redisClient.set(user.getId(), redisToken, myContansConfig.getTokenExpireTime());

		return R.ok(token);
	}

	/**
	 * @dec 发送短信验证码
	 * @date Mar 2, 2019
	 * @author gaochao
	 * @param username
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R sendSmsCode(String username) {
		Object smsCode = redisClient.get(username);
		if (smsCode != null) {
			return R.error("短信已发送，请稍后再试！");
		}

		smsCode = RandomStringUtils.randomNumeric(myContansConfig.getSmsCodeLength());
		String tkey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String password = DigestUtil.md5Hex(DigestUtil.md5Hex(myContansConfig.getSmsPassword()) + tkey);
		String content = String.format(myContansConfig.getSmsCodeContent(), smsCode);
		String url = String.format(myContansConfig.getSmsUrl(), myContansConfig.getSmsUsername(), tkey, password,
				username, content);

		boolean b = false;
		LogSms logSms = new LogSms();
		logSms.setMobile(username);
		logSms.setType(LogSms.TYPE_RESTPWD);
		String body = HttpUtil.get(url, 5000);
		if (StrUtil.isBlank(body)) {
			logSms.setResult(LogSms.RESULT_ERROR);
		} else if (!body.startsWith("1")) {
			logSms.setResult(LogSms.RESULT_ERROR);
			logSms.setBody(body.split(",")[1]);
		} else {
			logSms.setResult(LogSms.RESULT_SECCUSS);
			logSms.setBody(body.split(",")[1]);
			logSms.setContent(content);
			redisClient.set(username, smsCode, myContansConfig.getSmsCodeExpiretime());
			b = true;
		}
		logSmsService.save(logSms);

		if (b) {
			return R.ok();
		}

		return R.error();
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
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R restPwd(String username, String password, String code) {
		Object smsCode = redisClient.get(username);
		if (smsCode == null) {
			return R.error("验证码已过期！");
		}

		if (!StrUtil.equals(code, smsCode.toString())) {
			return R.error("验证码错误！");
		}

		User user = getOne("username", SqlEmnus.EQ, username);
		user.setPassword(EncrypUtil.encode(password));
		boolean update = updateById(user);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 发送短信
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param type
	 * @param mobile
	 * @param content
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void sendSms(Integer type, String mobile, String content) {
		String tkey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		String password = DigestUtil.md5Hex(DigestUtil.md5Hex(myContansConfig.getSmsPassword()) + tkey);
		String url = String.format(myContansConfig.getSmsUrl(), myContansConfig.getSmsUsername(), tkey, password,
				mobile, content);

		LogSms logSms = new LogSms();
		logSms.setMobile(mobile);
		logSms.setType(type);
		String body = HttpUtil.get(url, 5000);
		if (StrUtil.isBlank(body)) {
			logSms.setResult(LogSms.RESULT_ERROR);
		} else if (!body.startsWith("1")) {
			logSms.setResult(LogSms.RESULT_ERROR);
			logSms.setBody(body.split(",")[1]);
		} else {
			logSms.setResult(LogSms.RESULT_SECCUSS);
			logSms.setBody(body.split(",")[1]);
			logSms.setContent(content);
		}
		logSmsService.save(logSms);
	}

	/**
	 * @dec 上传头像
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param token
	 * @param file
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R upload(String token, MultipartFile file) {
		JSONObject jo = CommonUtil.decodeToken(token);
		User user = getById(jo.getStr("id"));
		String avatar = user.getAvatar();
		String upload;
		if (StrUtil.equals(avatar, myContansConfig.getWorkAvatar())) {
			upload = fdfsClient.upload(file);
		} else {
			upload = fdfsClient.updateFile(file, avatar.replaceAll(myContansConfig.getFdfsServer(), ""));
		}
		avatar = myContansConfig.getFdfsServer() + upload;

		user.setAvatar(avatar);
		boolean update = updateById(user);
		if (update) {
			return R.ok(avatar);
		}

		return R.error();
	}

}
