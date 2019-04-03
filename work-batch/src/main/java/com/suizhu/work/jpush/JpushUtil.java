package com.suizhu.work.jpush;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;

/**
 * 极光推送
 * 
 * @author gaochao
 * @date Mar 14, 2019
 */
public class JpushUtil {

	/**
	 * @dec android ios双平台推送
	 * @date Mar 14, 2019
	 * @author gaochao
	 * @param appKey
	 * @param masterSecret
	 * @param url
	 * @param usernames
	 * @param message
	 */
	public static void jpush(String appKey, String masterSecret, String url, Set<String> usernames, String message) {
		String Authorization = "Basic " + Base64.encode(appKey + ":" + masterSecret);
		HttpRequest httpRequest = HttpUtil.createRequest(Method.POST, url);
		httpRequest.timeout(5000);
		httpRequest.header("Authorization", Authorization);
		httpRequest.header("Content-Type", "application/json");

		Map<String, Object> params = new HashMap<>();
		params.put("platform", new String[] { "android", "ios" });

		Map<String, Set<String>> audience = new HashMap<>(1);
		audience.put("alias", usernames);
		params.put("audience", audience);

		Map<String, String> notification = new HashMap<>(1);
		notification.put("alert", message);
		params.put("notification", notification);

		httpRequest.body(JSONUtil.toJsonStr(params));
		httpRequest.execute();
	}

}
