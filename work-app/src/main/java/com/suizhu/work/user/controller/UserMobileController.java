package com.suizhu.work.user.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suizhu.common.core.R;
import com.suizhu.work.async.service.AsnycService;
import com.suizhu.work.entity.UserMobile;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 用户通讯录 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-29
 */
@RestController
@AllArgsConstructor
@RequestMapping("userMobile")
public class UserMobileController {

	private final AsnycService asnycService;

	/**
	 * @dec 扒取用户通讯录
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @param mobiles
	 * @param token
	 * @return
	 */
	@PostMapping
	public R mobile(String mobiles, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		if (StrUtil.isNotBlank(mobiles)) {
			JSONObject json = CommonUtil.decodeToken(token);
			List<UserMobile> list = JSONUtil.parseArray(mobiles).toList(UserMobile.class);

			if (CollUtil.isNotEmpty(list)) {
				asnycService.saveUserMobile(list, json.getStr("id"));
			}
		}

		return R.ok();
	}

}
