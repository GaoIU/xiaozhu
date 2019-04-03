package com.suizhu.cms.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@SpringBootConfiguration
@ConfigurationProperties(prefix = "suizhu.cms")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MyContansConfig {

	private String fdfsServer;

	private String cmsAvatar;

	private String workAvatar;

}
