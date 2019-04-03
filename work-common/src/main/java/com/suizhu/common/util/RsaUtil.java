package com.suizhu.common.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import cn.hutool.core.codec.Base64;

/**
 * RSA加密工具类
 * 
 * @author gaochao
 * @date Feb 28, 2019
 */
public class RsaUtil {

	private static final String PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDrqtbisF126BhwCDnnE2sF9v93ujWSWd1Byz95I09XWlSstdk4HBMkj+fQRJlLCYjiTtZ2eBy3bIbmMUiPq5xZ4lBGmD3wSIv/sX49/vWdRe10u0GgxJhl687n37WgV1GhViaZPXwaWsK1PcfhIFADPkqSMqg1CNu7RRi1mhaIikTeOZDCYYgypezYLDkIrd2l49va30m3+tPg0xRn2JNANMpwqiZPoRu3D8CtgjnQBlOhnmZJqdwX1QmJCUVx1VOtDijvvoeKggB98wSuSkEfi4oXmInw10yZlSV1V6f0Q/owCUfWbXK+j8w+xRgX3kYcGb82uPEINi2fYnd4CzXFAgMBAAECggEBALP4QyHajYXAuOjVX2KX8aMcfPtxuHeLKOyVymM4E98hnIhIVxoCVpwVSXJC8M3c+ivZCO5vy35H4rzn5gHRjBUYPzWqDaLxw/gAE+hETE1OmYgLeJLNj5RGADwTGyDXjf8nS1K7IYr+xLKj+4BwrKnNaCU9ijb2DNd2bOhCQ/MALUz4NR6HPKdIyJ15P8dH6+OZ7L0wpEQ7j3MTar43Q9zH05/daCs/w9LwzwEcps9uNThUTPg1DWUPEsTlBufFeRVuWLcviveMJ1ZBtzPTwTZs0sHoDOVKrCDo97vG3As1ivFQoX1Spnbp/7oEvo+3RMGiZah3Qhbd396G6+wL2gECgYEA/Gykd+DQsRWM3qhdD3bywYDbDU9Yppfe/XqTdGmkvw8aqXy07S81pzFS7ezalsxvplhZ/2XM99r06Nm3fpNy1LUjtpbDJ1uNmBrIyeIdbxpoPDgcSvsiw7SlPwHrM4cDccPs3rmYaypmdxPBp3+HAefakMOBHICgjXZI0uhtxTcCgYEA7wFudRT7GFlOK8gGVmpoWh8csUqOpnS70ZPmwZ8Xqi+7mYxCcLwKpDjdYt2RK/RsGHcMuP6yTiu09QsGGFE6D9qUHQpfNhSlrphQyjy7m6AS45FFgAmN0QTpOhGMai+eC9MXE5lKsr365QZTWuJgp8JzW+X5vYqx05NLrSOgWuMCgYEAig3VKacwHBnAMKp+Mw4IGKKjqzThNEPNdm6KpNl+e+wD1fxZa3staeKFC5FFv8HRsnWRyyffIROEbLFeNQDphcIH5T2xQB5HY5AKjrd3B6O2rHC3sJ6HVee/lKU1ALpvvNG+A9YXsUzAO5H3kOcjbHxkwlXakD8dECFIFOl/16MCgYEAqrE2Bu5OCey19HSteggSsNJpdoEN8fmKrbnYYs3RY90lORs/1C2n1/M4wUQGSwMMNEjJ0FEuXL/ARAV0e+d+9RWhFEB1ETWbwl7LzDOltRaUhuwYW8H3A6FjQ/KGesu5Ly6522huPcuKpQhofY8Rgp5QLwDG5MRqDfWzZVr9SgsCgYBlmd0h+SZZTnc0uVBFVbPe/VBNpttE3BwKrOGnrG+exsIjsUe1cE3pYk35jPS2eG57CZ1a7NfARAyvC3f67+G2nKCdJRGvum6/ly79knp8tfuEYFKU1VfbAvfrYQaiT9CPnPa2Bs8oyMNTkuLZUBemx7RMEiSQQpq8sLifBMMmxw==";

	private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA66rW4rBddugYcAg55xNrBfb/d7o1klndQcs/eSNPV1pUrLXZOBwTJI/n0ESZSwmI4k7Wdngct2yG5jFIj6ucWeJQRpg98EiL/7F+Pf71nUXtdLtBoMSYZevO59+1oFdRoVYmmT18GlrCtT3H4SBQAz5KkjKoNQjbu0UYtZoWiIpE3jmQwmGIMqXs2Cw5CK3dpePb2t9Jt/rT4NMUZ9iTQDTKcKomT6Ebtw/ArYI50AZToZ5mSancF9UJiQlFcdVTrQ4o776HioIAffMErkpBH4uKF5iJ8NdMmZUldVen9EP6MAlH1m1yvo/MPsUYF95GHBm/NrjxCDYtn2J3eAs1xQIDAQAB";

	/**
	 * @dec 生成秘钥对
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @return
	 */
	public static KeyPair getKeyPair() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			return keyPairGenerator.generateKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 获取公钥
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param keyPair
	 * @return
	 */
	public static String getPublicKey(KeyPair keyPair) {
		PublicKey publicKey = keyPair.getPublic();
		return Base64.encode(publicKey.getEncoded());
	}

	/**
	 * @dec 获取私钥
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param keyPair
	 * @return
	 */
	public static String getPrivateKey(KeyPair keyPair) {
		PrivateKey privateKey = keyPair.getPrivate();
		return Base64.encode(privateKey.getEncoded());
	}

	/**
	 * @dec 把公钥转换成PublicKey对象
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param publicKey
	 * @return
	 */
	public static PublicKey toPublicKey(String publicKey) {
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decode(publicKey));
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(x509EncodedKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 把私钥转换成PrivateKey对象
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param privateKey
	 * @return
	 */
	public static PrivateKey toPrivateKey(String privateKey) {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 公钥加密
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param content
	 * @return
	 */
	public static String encoder(String content) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, toPublicKey(PUBLIC_KEY));
			byte[] doFinal = cipher.doFinal(content.getBytes());
			return Base64.encode(doFinal);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 公钥加密
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param content
	 * @param publicKey
	 * @return
	 */
	public static String encoder(String content, String publicKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, toPublicKey(publicKey));
			byte[] doFinal = cipher.doFinal(content.getBytes());
			return Base64.encode(doFinal);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 私钥解密
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param content
	 * @return
	 */
	public static String decode(String content) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, toPrivateKey(PRIVATE_KEY));
			byte[] doFinal = cipher.doFinal(Base64.decode(content));
			return new String(doFinal);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 私钥解密
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param content
	 * @param privateKey
	 * @return
	 */
	public static String decode(String content, String privateKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, toPrivateKey(privateKey));
			byte[] doFinal = cipher.doFinal(Base64.decode(content));
			return new String(doFinal);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
