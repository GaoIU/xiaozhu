package com.suizhu.common.core.encryp;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;

import cn.hutool.core.util.StrUtil;

public class MyEncryptablePropertyDetector implements EncryptablePropertyDetector {

	public static final String ENCODED_PASSWORD_HINT = "{suizhu}";

	@Override
	public boolean isEncrypted(String property) {
		if (StrUtil.isNotBlank(property)) {
			return property.startsWith(ENCODED_PASSWORD_HINT);
		}
		return false;
	}

	@Override
	public String unwrapEncryptedValue(String property) {
		return property.substring(ENCODED_PASSWORD_HINT.length());
	}

}
