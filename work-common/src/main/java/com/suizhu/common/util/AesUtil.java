package com.suizhu.common.util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;

/**
 * AES加密工具类
 * 
 * @author gaochao
 * @date Feb 19, 2019
 */
public class AesUtil {

	private static final String AES_KEY = "zRupZjLXxlVsLy2AKfh2xzYs2SNwEV7k1cfezauSK4o=";

	/**
	 * @dec 生成AES密钥
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @return
	 */
	public static String getAesKey() {
		KeyGenerator keyGenerator;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		keyGenerator.init(256);
		SecretKey secretKey = keyGenerator.generateKey();
		return Base64.encode(secretKey.getEncoded());
	}

	/**
	 * @dec 生成AES密钥
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param keySize
	 * @return
	 */
	public static String getAesKey(int keySize) {
		KeyGenerator keyGenerator;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		keyGenerator.init(keySize);
		SecretKey secretKey = keyGenerator.generateKey();
		return Base64.encode(secretKey.getEncoded());
	}

	/**
	 * @dec 加密
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param source
	 * @return
	 */
	public static String encoder(String source) {
		if (StrUtil.isBlank(source)) {
			return null;
		}

		byte[] bytes = Base64.decode(AES_KEY.getBytes());
		SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			return Base64.encode(cipher.doFinal(source.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 加密
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param source
	 * @return
	 */
	public static String encoder(byte[] source) {
		if (source == null || source.length == 0) {
			return null;
		}

		byte[] bytes = Base64.decode(AES_KEY.getBytes());
		SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			return Base64.encode(cipher.doFinal(source));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 加密
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param source
	 * @param aesKey
	 * @return
	 */
	public static String encoder(String source, String aesKey) {
		if (StrUtil.isBlank(source) || StrUtil.isBlank(aesKey)) {
			return null;
		}

		byte[] bytes = Base64.decode(aesKey.getBytes());
		SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			return Base64.encode(cipher.doFinal(source.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 加密
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param source
	 * @param aesKey
	 * @return
	 */
	public static String encoder(byte[] source, String aesKey) {
		if (source == null || source.length == 0 || StrUtil.isBlank(aesKey)) {
			return null;
		}

		byte[] bytes = Base64.decode(aesKey.getBytes());
		SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			return Base64.encode(cipher.doFinal(source));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 解密
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param cs
	 * @return
	 */
	public static String decode(CharSequence cs) {
		if (cs == null || cs.length() == 0) {
			return null;
		}

		try {
			Cipher cipher = Cipher.getInstance("AES");
			byte[] bytes = Base64.decode(AES_KEY);
			SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			return new String(cipher.doFinal(Base64.decode(cs.toString())));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 解密
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param cs
	 * @return
	 */
	public static String decode(byte[] cs) {
		if (cs == null || cs.length == 0) {
			return null;
		}

		try {
			Cipher cipher = Cipher.getInstance("AES");
			byte[] bytes = Base64.decode(AES_KEY);
			SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			return new String(cipher.doFinal(Base64.decode(cs)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 解密
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param cs
	 * @param aesKey
	 * @return
	 */
	public static String decode(CharSequence cs, String aesKey) {
		if (cs == null || cs.length() == 0) {
			return null;
		}

		try {
			Cipher cipher = Cipher.getInstance("AES");
			byte[] bytes = Base64.decode(aesKey);
			SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			return new String(cipher.doFinal(Base64.decode(cs.toString())));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 解密
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param cs
	 * @param aesKey
	 * @return
	 */
	public static String decode(byte[] cs, String aesKey) {
		if (cs == null || cs.length == 0) {
			return null;
		}

		try {
			Cipher cipher = Cipher.getInstance("AES");
			byte[] bytes = Base64.decode(aesKey);
			SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			return new String(cipher.doFinal(Base64.decode(cs)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
