package com.suizhu.common.core;

import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;

import com.suizhu.common.util.StrUtil;

public class MapWrapper extends org.apache.ibatis.reflection.wrapper.MapWrapper {

	public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
		super(metaObject, map);
	}

	@Override
	public String findProperty(String name, boolean useCamelCaseMapping) {
		if (useCamelCaseMapping) {
			return StrUtil.underlineToCamel(name);
		}
		return name;
	}

}
