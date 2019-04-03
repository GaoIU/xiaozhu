package com.suizhu.cms.config;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		setFieldValByName("createTime", LocalDateTime.now(), metaObject);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
	}

}
