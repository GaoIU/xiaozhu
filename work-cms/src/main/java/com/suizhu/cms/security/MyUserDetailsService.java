package com.suizhu.cms.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.cms.entity.SysResource;
import com.suizhu.cms.entity.SysUser;
import com.suizhu.cms.service.SysResourceService;
import com.suizhu.cms.service.SysUserService;
import com.suizhu.common.constant.emnus.SqlEmnus;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

	private final SysUserService sysUserService;

	private final SysResourceService sysResourceService;

	private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SysUser sysUser = sysUserService.getOne("username", SqlEmnus.EQ, username, "id", "password");

		return new User(username, sysUser.getPassword(), authorities(sysUser.getId()));
	}

	private Set<GrantedAuthority> authorities(String sysUserId) {
		QueryWrapper<SysResource> wrapper = new QueryWrapper<>();
		wrapper.eq("status", SysResource.STATUS_NORMAL);
		wrapper.select("code", "method");
		List<SysResource> list = sysResourceService.findByWrapper(sysUserId, wrapper);

		Set<GrantedAuthority> authorities;
		if (CollUtil.isEmpty(list)) {
			authorities = new HashSet<>(1);
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("GUST");
			authorities.add(grantedAuthority);
		} else {
			authorities = new HashSet<>(list.size());
			list.forEach(r -> {
				GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(r.getCode() + "@" + r.getMethod());
				authorities.add(grantedAuthority);
			});
		}

		return authorities;
	}

	public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
		boolean permission = false;
		String requestURI = request.getRequestURI();
		String method = request.getMethod();

		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			String sysUserId = sysUserService.getOne("username", SqlEmnus.EQ, username, "id").getId();

			QueryWrapper<SysResource> wrapper = new QueryWrapper<>();
			wrapper.eq("status", SysResource.STATUS_NORMAL);
			wrapper.select("method", "url");
			List<SysResource> list = sysResourceService.findByWrapper(sysUserId, wrapper);
			if (CollUtil.isNotEmpty(list)) {
				permission = list.stream().anyMatch(
						r -> antPathMatcher.match(requestURI, r.getUrl()) && StrUtil.equals(method, r.getMethod()));
			}
		}

		return permission;
	}

}
