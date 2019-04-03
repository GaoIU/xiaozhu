package com.suizhu.cms.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.ext.jsp.TaglibFactory;
import lombok.AllArgsConstructor;

@SpringBootConfiguration
@AllArgsConstructor
public class TldConfig implements WebMvcConfigurer {

	private final FreeMarkerConfigurer configurer;

	@PostConstruct
	public void freeMarkerConfigurer() {
		List<String> tlds = new ArrayList<String>(1);
		tlds.add("/static/other/tag/security.tld");
		TaglibFactory taglibFactory = configurer.getTaglibFactory();
		taglibFactory.setClasspathTlds(tlds);

		if (taglibFactory.getObjectWrapper() == null) {
			taglibFactory.setObjectWrapper(configurer.getConfiguration().getObjectWrapper());
		}
	}

}
