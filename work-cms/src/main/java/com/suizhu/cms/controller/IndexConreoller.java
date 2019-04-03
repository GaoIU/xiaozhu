package com.suizhu.cms.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.cms.entity.SysResource;
import com.suizhu.cms.entity.SysRole;
import com.suizhu.cms.entity.SysUser;
import com.suizhu.cms.service.SysResourceService;
import com.suizhu.cms.service.SysRoleService;
import com.suizhu.cms.service.SysUserService;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.util.EncrypUtil;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class IndexConreoller {

	private final SysUserService sysUserService;

	private final AuthenticationManager authenticationManager;

	private final SysResourceService sysResourceService;

	private final SysRoleService sysRoleService;

	/**
	 * @dec 跳转至登录页面
	 * @date Feb 20, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/")
	public ModelAndView homePage() {
		return new ModelAndView("sys/login");
	}

	/**
	 * @dec 后台用户登录
	 * @date Feb 20, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@PostMapping("/signIn")
	public R signIn(@RequestBody Map<String, Object> params, HttpSession session) {
		String username = MapUtil.getStr(params, "username");
		String password = MapUtil.getStr(params, "password");
		if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
			return R.error("用户名或密码不能为空！");
		}

		SysUser sysUser = sysUserService.getOne("username", SqlEmnus.EQ, username);
		if (sysUser != null && EncrypUtil.matches(password, sysUser.getPassword())) {
			if (SysUser.STATUS_DISABLE == sysUser.getStatus()) {
				return R.error("该账号已被禁用，请联系管理员！");
			}

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
					password);
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			session.setAttribute(SysUser.DEFAULT_SESSION_KEY, sysUser);

			return R.ok("index");
		}

		return R.error("用户名或密码错误！");
	}

	/**
	 * @dec 跳转至首页
	 * @date Feb 20, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/index")
	public ModelAndView index() {
		return new ModelAndView("sys/index");
	}

	/**
	 * @dec 获取首页导航菜单
	 * @date Feb 20, 2019
	 * @author gaochao
	 * @return
	 */
	@PostMapping("/index")
	public R getMenu(HttpSession session) {
		SysUser sysUser = (SysUser) session.getAttribute(SysUser.DEFAULT_SESSION_KEY);
		QueryWrapper<SysResource> wrapper = new QueryWrapper<>();
		wrapper.eq("status", SysResource.STATUS_NORMAL);
		wrapper.and(i -> i.eq("type", SysResource.TYPE_MENU));
		wrapper.and(i -> i.isNull("parent_id").or().eq("parent_id", ""));
		wrapper.select("id", "name", "url", "icon");
		wrapper.orderByAsc("sort");
		wrapper.orderByDesc("create_time");
		List<SysResource> list = sysResourceService.findByWrapper(sysUser.getId(), wrapper);

		return R.ok(sysResourceService.getMenu(list, "childList", SysResource.TYPE_MENU));
	}

	/**
	 * @dec 跳转至个人信息页面
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param session
	 * @return
	 */
	@GetMapping("/sysUserInfo")
	public ModelAndView sysUserInfo(HttpSession session) {
		SysUser sysUser = (SysUser) session.getAttribute(SysUser.DEFAULT_SESSION_KEY);
		List<SysRole> list = sysRoleService.findBySysUserId(sysUser.getId());

		ModelAndView mav = new ModelAndView("sys/userinfo");
		mav.addObject("sysUser", sysUser);
		mav.addObject("roles", list);
		return mav;
	}

	/**
	 * @dec 修改个人信息
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysUser
	 * @return
	 */
	@PutMapping("/sysUserInfo")
	public R changeInfo(HttpSession session, @Valid @RequestBody SysUser sysUser) {
		String id = sysUser.getId();
		boolean checkNickName = sysUserService.checkOnly(id, "nick_name", sysUser.getNickName());
		if (!checkNickName) {
			return R.error("该昵称已被使用！");
		}
		boolean checkMobile = sysUserService.checkOnly(id, "mobile", sysUser.getMobile());
		if (!checkMobile) {
			return R.error("该手机号已存在！");
		}

		boolean update = sysUserService.updateById(sysUser);
		if (update) {
			session.setAttribute(SysUser.DEFAULT_SESSION_KEY, sysUserService.getById(id));
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 上传后台用户头像
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param avatar
	 * @param sysUserId
	 * @return
	 */
	@PostMapping("/upload")
	public R uploadAvatar(MultipartFile avatar, String sysUserId) {
		if (avatar == null || avatar.isEmpty() || StrUtil.isBlank(sysUserId)) {
			return R.error("非法请求！");
		}

		return sysUserService.uploadAvatar(avatar, sysUserId);
	}

	/**
	 * @dec 跳转至修改密码页面
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/resetPwd")
	public ModelAndView restPwd() {
		return new ModelAndView("/sys/resetPwd");
	}

	/**
	 * @dec 修改密码
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param session
	 * @param password
	 * @param oldPwd
	 * @return
	 */
	@PostMapping("/resetPwd")
	public R changePwd(HttpSession session, String password, String oldPwd) {
		SysUser sysUser = (SysUser) session.getAttribute(SysUser.DEFAULT_SESSION_KEY);
		if (!EncrypUtil.matches(oldPwd, sysUser.getPassword())) {
			return R.error("旧密码输入错误！");
		}

		sysUser.setPassword(EncrypUtil.encode(password));
		boolean update = sysUserService.updateById(sysUser);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 验证密码
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param session
	 * @param password
	 * @return
	 */
	@PostMapping("/checkPwd")
	public R checkPwd(HttpSession session, String password) {
		SysUser sysUser = (SysUser) session.getAttribute(SysUser.DEFAULT_SESSION_KEY);
		return R.ok(EncrypUtil.matches(password, sysUser.getPassword()));
	}

	/**
	 * @dec 验证昵称
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param nickName
	 * @param sysUserId
	 * @return
	 */
	@PostMapping("/checkNickName")
	public R checkNickName(String nickName, String sysUserId) {
		boolean checkOnly = sysUserService.checkOnly(sysUserId, "nick_name", nickName);
		return R.ok(checkOnly);
	}

	/**
	 * @dec 验证手机号码
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param mobile
	 * @param sysUserId
	 * @return
	 */
	@PostMapping("/checkMobile")
	public R checkMobile(String mobile, String sysUserId) {
		boolean checkOnly = sysUserService.checkOnly(sysUserId, "mobile", mobile);
		return R.ok(checkOnly);
	}

}
