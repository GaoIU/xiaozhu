package com.suizhu.work.config;

import java.io.IOException;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 将null转换位空字符串Jackson配置
 * 
 * @author gaochao
 * @date Mar 1, 2019
 */
@SpringBootConfiguration
public class JacksonWriteConfig {

	@Bean
	@Primary
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {

			@Override
			public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				gen.writeString("");
			}
		});
		return objectMapper;
	}

}
