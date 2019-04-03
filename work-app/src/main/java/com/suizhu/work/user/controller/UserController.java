package com.suizhu.work.user.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.common.util.RsaUtil;
import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.Doorway;
import com.suizhu.work.entity.User;
import com.suizhu.work.entity.UserMessage;
import com.suizhu.work.user.service.UserMessageService;
import com.suizhu.work.user.service.UserOrgService;
import com.suizhu.work.user.service.UserService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("user")
public class UserController {

	private final UserService userService;

	private final UserOrgService userOrgService;

	private final UserMessageService userMessageService;

	private final DoorwayService doorwayService;

	/**
	 * @dec 个人中心
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param token
	 * @return
	 */
	@GetMapping
	public R info(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject jo = CommonUtil.decodeToken(token);
		User user = userService.getById(jo.getStr("id"));
		Map<String, Object> data = new HashMap<>(7);
		data.put("realName", user.getRealName());
		data.put("username", user.getUsername());
		data.put("avatar", user.getAvatar());
		data.put("orgName", user.getDection());
		data.put("email", user.getEmail());

		boolean exist = userOrgService.exist("user_id", SqlEmnus.EQ, jo.getStr("id"));
		data.put("hasUser", exist);

		QueryWrapper<UserMessage> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id", jo.getStr("id")).and(i -> i.eq("remind", UserMessage.REMIND_NOT));
		int message = userMessageService.count(queryWrapper);
		data.put("message", message);

		return R.ok(data);
	}

	/**
	 * @dec 修改头像
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param token
	 * @param file
	 * @return
	 */
	@PostMapping("/avatar")
	public R upload(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException();
		}

		return userService.upload(token, file);
	}

	/**
	 * @dec 修改用户信息
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param token
	 * @param realName
	 * @param orgName
	 * @return
	 */
	@PutMapping
	public R edit(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, String realName, String orgName) {
		if (StrUtil.isBlank(realName) || StrUtil.isBlank(orgName)) {
			throw new BadRequestException();
		}
		JSONObject jo = CommonUtil.decodeToken(token);
		User user = userService.getById(jo.getStr("id"));
		user.setRealName(realName);
		user.setDection(orgName);
		boolean update = userService.updateById(user);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 获取消息中心列表
	 * @date Mar 14, 2019
	 * @author gaochao
	 * @param token
	 * @return
	 */
	@GetMapping("/message")
	public R message(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "9") Integer size,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject jo = CommonUtil.decodeToken(token);
		QueryWrapper<UserMessage> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id", jo.getStr("id")).orderByDesc("create_time");
		IPage<UserMessage> page = new Page<>(current, size);
		page = userMessageService.page(page, queryWrapper);
		List<UserMessage> list = page.getRecords();
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		List<Map<String, Object>> datas = list.stream().map(um -> {
			Map<String, Object> data = CommonUtil.beanToMapFl(um, "type", "userId");
			data.replace("createTime", um.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			Doorway doorway = doorwayService.getOne("id", SqlEmnus.EQ, um.getDoorwayId(), "name", "category_id");
			data.put("name", doorway.getName());
			data.put("categoryId", doorway.getCategoryId());
			return data;
		}).collect(Collectors.toList());

		return R.ok(datas);
	}

	/**
	 * @dec 读取消息
	 * @date Mar 20, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/message/{id}")
	public R readMsg(@PathVariable("id") String id) {
		UserMessage userMessage = userMessageService.getById(id);
		userMessage.setRemind(UserMessage.REMIND_YES);
		boolean update = userMessageService.updateById(userMessage);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 绑定邮箱
	 * @date Mar 15, 2019
	 * @author gaochao
	 * @param email
	 * @param emailPassword
	 * @param token
	 * @return
	 */
	@PostMapping("/mail")
	public R email(String email, String emailPassword, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		if (StrUtil.isBlank(email) || StrUtil.isBlank(emailPassword)) {
			throw new BadRequestException();
		}

		if (!CommonUtil.checkEmail(email)) {
			throw new BadRequestException("邮箱不合法！");
		}

		User user = userService.getById(CommonUtil.decodeToken(token).getStr("id"));
		emailPassword = RsaUtil.encoder(emailPassword);
		user.setEmail(email);
		user.setEmailPassword(emailPassword);
		boolean update = userService.updateById(user);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 删除通知消息
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @return
	 */
	@DeleteMapping("/message/{id}")
	public R delMsg(@PathVariable("id") String id) {
		boolean remove = userMessageService.removeById(id);
		if (remove) {
			return R.ok();
		}

		return R.error();
	}

}
