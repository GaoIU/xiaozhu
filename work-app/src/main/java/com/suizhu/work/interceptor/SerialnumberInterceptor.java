package com.suizhu.work.interceptor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.suizhu.common.exception.ForbiddenException;
import com.suizhu.work.config.MyContansConfig;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.AllArgsConstructor;

/**
 * 签名认证拦截器
 * 
 * @author gaochao
 * @date Mar 1, 2019
 */
@Component
@AllArgsConstructor
public class SerialnumberInterceptor implements HandlerInterceptor {

	private final MyContansConfig myContansConfig;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Map<String, Object> args;
		Map<String, String[]> parameterMap = request.getParameterMap();
		if (MapUtil.isEmpty(parameterMap)) {
			args = new HashMap<>(0);
		} else {
			args = new HashMap<>(parameterMap.size());
			parameterMap.forEach((k, v) -> {
				if (ArrayUtil.isNotEmpty(v) && StrUtil.isNotBlank(v[0])) {
					args.put(k, v[0]);
				}
			});
		}

		String appId = MapUtil.getStr(args, "appId");
		Long timestamp = MapUtil.getLong(args, "timestamp");
		if (StrUtil.isBlank(appId) || timestamp == null) {
			throw new ForbiddenException("无效签名");
		}

		LocalDateTime now = LocalDateTime.now();
		long agoTime = now.plusSeconds(-10).toInstant(ZoneOffset.of("+8")).toEpochMilli();
		long willTime = now.plusSeconds(10).toInstant(ZoneOffset.of("+8")).toEpochMilli();
		if (timestamp < agoTime || timestamp > willTime) {
			throw new ForbiddenException("无效签名");
		}
		args.remove("appId");
		args.remove("timestamp");

		String serverAppId;
		if (MapUtil.isEmpty(args)) {
			serverAppId = DigestUtil.md5Hex(myContansConfig.getAppId() + timestamp);
		} else {
			Map<String, Object> sortMap = CommonUtil.comparingByValue(args, true);
			StringBuffer sb = new StringBuffer(myContansConfig.getAppId() + timestamp);
			sortMap.forEach((k, v) -> {
				sb.append(v.toString());
			});
			serverAppId = DigestUtil.md5Hex(sb.toString());
		}

		if (!StrUtil.equals(appId, serverAppId)) {
			throw new ForbiddenException("无效签名");
		}

		return true;
	}

}
