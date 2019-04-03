package com.suizhu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.suizhu.common.core.encryp.MyEncryptablePropertyDetector;
import com.suizhu.common.core.encryp.MyEncryptablePropertyResolver;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;

@SpringBootApplication
@MapperScan("com.suizhu.**.mapper")
public class WorkCmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkCmsApplication.class, args);
	}

	@Bean(name = "encryptablePropertyDetector")
	public EncryptablePropertyDetector encryptablePropertyDetector() {
		return new MyEncryptablePropertyDetector();
	}

	@Bean(name = "encryptablePropertyResolver")
	public EncryptablePropertyResolver encryptablePropertyResolver() {
		return new MyEncryptablePropertyResolver();
	}

}
