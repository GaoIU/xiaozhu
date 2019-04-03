package com.suizhu.work.util;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.suizhu.common.util.RsaUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 公共工具类
 * 
 * @author gaochao
 * @date Mar 1, 2019
 */
public class CommonUtil {

	/**
	 * @dec 根据Map的值排序
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param args
	 * @param asc
	 * @return
	 */
	public static Map<String, Object> comparingByValue(Map<String, Object> args, boolean asc) {
		if (MapUtil.isEmpty(args)) {
			return null;
		}

		Map<String, Object> resMap = new LinkedHashMap<>(args.size());
		if (asc) {
			args.entrySet().stream().sorted((Comparator<Map.Entry<String, Object>>) (c1, c2) -> {
				return c1.getValue().toString().compareTo(c2.getValue().toString());
			}).forEachOrdered(e -> {
				resMap.put(e.getKey(), e.getValue());
			});
		} else {
			args.entrySet().stream().sorted(((Comparator<Map.Entry<String, Object>>) (c1, c2) -> {
				return c1.getValue().toString().compareTo(c2.getValue().toString());
			}).reversed()).forEachOrdered(e -> {
				resMap.put(e.getKey(), e.getValue());
			});
		}

		return resMap;
	}

	/**
	 * @dec 验证手机号码
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param mobile
	 * @return
	 */
	public static boolean checkMobile(String mobile) {
		if (StrUtil.isBlank(mobile)) {
			return false;
		}

		return mobile.matches("0?(13|14|15|17|18|19)[0-9]{9}");
	}

	/**
	 * @dec 验证邮箱
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		if (StrUtil.isBlank(email)) {
			return false;
		}

		return email.matches("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
	}

	/**
	 * @dec 解析Token
	 * @date Mar 4, 2019
	 * @author gaochao
	 * @param token
	 * @return
	 */
	public static JSONObject decodeToken(String token) {
		if (StrUtil.isBlank(token)) {
			return null;
		}

		JSONObject parseObj = JSONUtil.parseObj(RsaUtil.decode(token));
		return parseObj;
	}

	/**
	 * @dec Bean转Map过滤字段
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param obj
	 * @param fls
	 * @return
	 */
	public static Map<String, Object> beanToMapFl(Object obj, String... fls) {
		Map<String, Object> beanToMap = BeanUtil.beanToMap(obj);
		for (String fl : fls) {
			beanToMap.remove(fl);
		}
		return beanToMap;
	}

	/**
	 * @dec 判断是否是今天
	 * @date Mar 10, 2019
	 * @author gaochao
	 * @param date
	 * @return
	 */
	public static boolean isToday(LocalDate date) {
		LocalDate today = LocalDate.now();
		return today.isEqual(date);
	}

	/**
	 * @dec 判断星期几
	 * @date Mar 10, 2019
	 * @author gaochao
	 * @param date
	 * @return
	 */
	public static int dayOfWeek(LocalDate date) {
		return null == date ? 0 : date.getDayOfWeek().getValue();
	}

	/**
	 * @dec 这个月有多少天
	 * @date Mar 10, 2019
	 * @author gaochao
	 * @param year
	 * @param month
	 * @return
	 */
	public static int monthDays(int year, int month) {
		LocalDate date = LocalDate.of(year, month, 1);
		return date.lengthOfMonth();
	}

	/**
	 * @dec 过滤emoji 或者 其他非文字类型的字符
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source) {
		source = source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");
		if (!containsEmoji(source)) {
			return source;// 如果不包含，直接返回
		}
		// 到这里铁定包含
		StringBuilder buf = null;

		int len = source.length();

		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);

			if (isEmojiCharacter(codePoint)) {
				if (buf == null) {
					buf = new StringBuilder(source.length());
				}

				buf.append(codePoint);
			} else {
				buf.append("*");
			}
		}

		if (buf == null) {
			return source;// 如果没有找到 emoji表情，则返回源字符串
		} else {
			if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
				buf = null;
				return source;
			} else {
				return buf.toString();
			}
		}

	}

	/**
	 * @dec 检测是否有emoji字符
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @param source
	 * @return
	 */
	public static boolean containsEmoji(String source) {
		if (StrUtil.isBlank(source)) {
			return false;
		}

		int len = source.length();

		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);

			if (isEmojiCharacter(codePoint)) {
				// do nothing，判断到了这里表明，确认有表情字符
				return true;
			}
		}

		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}

}
