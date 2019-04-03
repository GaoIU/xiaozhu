package com.suizhu.work.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.suizhu.common.core.RedisClient;
import com.suizhu.common.exception.NotfoundException;
import com.suizhu.common.exception.UnauthorizedException;
import com.suizhu.common.util.RsaUtil;
import com.suizhu.work.entity.User;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

	private final RedisClient redisClient;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String clientId = request.getHeader("Client-Id");
		if (StrUtil.isBlank(clientId)) {
			throw new NotfoundException("无效请求！");
		}

		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (StrUtil.isBlank(token)) {
			throw new UnauthorizedException("来者何人，报上名来！");
		}

		Object userId = redisClient.get(clientId);
		if (userId == null) {
			throw new UnauthorizedException("来者何人，报上名来！");
		}

		Object redisToken = redisClient.get(userId.toString());
		if (redisToken == null) {
			throw new UnauthorizedException("登录信息已过期！");
		}

		JSONObject parseObj;
		try {
			parseObj = JSONUtil.parseObj(RsaUtil.decode(redisToken.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnauthorizedException("来者何人，报上名来！");
		}
		if (User.STATUS_DISABLE == parseObj.getInt("status")) {
			throw new UnauthorizedException("该账号已被禁用，请联系客服人员！");
		}

		if (!StrUtil.equals(DigestUtil.md5Hex(token), parseObj.getStr("token"))) {
			throw new UnauthorizedException("该账号已在别处登录，请重新登录！");
		}

		return true;
	}

}
