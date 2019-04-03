package com.suizhu.common.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

import com.github.tobato.fastdfs.FdfsClientConfig;

/**
 * fdfs配置
 * 
 * @author gaochao
 * @date Feb 18, 2019
 */
@SpringBootConfiguration
@Import(FdfsClientConfig.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class FdfsConfig {

}
