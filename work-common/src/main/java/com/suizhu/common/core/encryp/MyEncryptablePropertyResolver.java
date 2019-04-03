package com.suizhu.common.core.encryp;

import com.suizhu.common.util.AesUtil;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;

import cn.hutool.core.util.StrUtil;

public class MyEncryptablePropertyResolver implements EncryptablePropertyResolver {

	@Override
	public String resolvePropertyValue(String value) {
		if (StrUtil.isNotBlank(value) && value.startsWith(MyEncryptablePropertyDetector.ENCODED_PASSWORD_HINT)) {
			value = value.substring(MyEncryptablePropertyDetector.ENCODED_PASSWORD_HINT.length());
			return AesUtil.decode(value);
		}
		return value;
	}

}
