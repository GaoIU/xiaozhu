package com.suizhu.cms.security;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;

/**
 * WebSecurity配置
 * 
 * @author gaochao
 * @date Feb 20, 2019
 */
@SpringBootConfiguration
@AllArgsConstructor
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

	private final MyUserDetailsService myUserDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**/*", "/images/**/*", "/js/**/*", "/other/**/*");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().loginPage("/").usernameParameter("username").passwordParameter("password").and().csrf()
				.disable().headers().frameOptions().sameOrigin().and().sessionManagement().invalidSessionUrl("/")
				.maximumSessions(1).and().and().authorizeRequests().antMatchers(HttpMethod.GET, "/").permitAll()
				.antMatchers(HttpMethod.POST, "/signIn").permitAll()
				.antMatchers("/index", "/sysUserInfo", "/upload", "/resetPwd", "/checkPwd", "/checkNickName",
						"/checkMobile")
				.authenticated().anyRequest().access("@myUserDetailsService.hasPermission(request, authentication)");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

}
