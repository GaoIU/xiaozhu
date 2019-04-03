package com.suizhu.common.util;

import java.util.regex.Pattern;

/**
 * 加密工具类
 * 
 * @author gaochao
 * @date Feb 20, 2019
 */
public class EncrypUtil {

	private static final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

	/**
	 * @dec BC加密
	 * @date Feb 20, 2019
	 * @author gaochao
	 * @param rawPassword 需要加密的明文密码
	 * @return
	 */
	public static String encode(CharSequence rawPassword) {
		int strength = -1;
		if (strength != -1 && (strength < BCrypt.MIN_LOG_ROUNDS || strength > BCrypt.MAX_LOG_ROUNDS)) {
			throw new IllegalArgumentException("Bad strength");
		}
		String salt;
		if (strength > 0) {
			salt = BCrypt.gensalt(strength);
		} else {
			salt = BCrypt.gensalt();
		}
		return BCrypt.hashpw(rawPassword.toString(), salt);
	}

	/**
	 * @dec BC加密验证
	 * @date Feb 20, 2019
	 * @author gaochao
	 * @param rawPassword     明文密码
	 * @param encodedPassword 加密过的密码
	 * @return
	 */
	public static boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (encodedPassword == null || encodedPassword.length() == 0) {
			return false;
		}

		if (rawPassword == null || rawPassword.length() == 0) {
			return false;
		}

		if (!BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
			return false;
		}

		return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
	}

}
