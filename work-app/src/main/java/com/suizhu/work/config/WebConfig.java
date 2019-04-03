package com.suizhu.work.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.suizhu.work.interceptor.AuthenticationInterceptor;
import com.suizhu.work.interceptor.SerialnumberInterceptor;

import lombok.AllArgsConstructor;

@SpringBootConfiguration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final AuthenticationInterceptor authenticationInterceptor;

	private final SerialnumberInterceptor serialnumberInterceptor;

	private static final String[] EXCLUDE_PATH = { "/", "/signIn", "/signToken", "/forget", "/restPwd" };

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(serialnumberInterceptor).addPathPatterns("/**");

		registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**").excludePathPatterns(EXCLUDE_PATH);
	}

}
