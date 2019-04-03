package com.suizhu.common.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.suizhu.common.util.StrUtil;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

/**
 * Redis客户端
 * 
 * @author gaochao
 * @date Feb 18, 2019
 */
@Component
@AllArgsConstructor
public class RedisClient {

	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * @dec 缓存放入
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key, Object value) {
		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @dec 缓存放入并设置时间
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param key
	 * @param value
	 * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
	 * @return
	 */
	public boolean set(String key, Object value, long time) {
		try {
			if (time > 0) {
				redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
			} else {
				redisTemplate.opsForValue().set(key, value);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @dec 缓存放入并设置时间
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param key
	 * @param value
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public boolean set(String key, Object value, long timeout, TimeUnit unit) {
		try {
			redisTemplate.opsForValue().set(key, value, timeout, unit);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @dec 指定缓存失效时间
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param key
	 * @param time 单位：秒
	 * @return
	 */
	public boolean expire(String key, long time) {
		try {
			if (time > 0) {
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @dec 获取过期时间,时间(秒) 返回0代表为永久有效
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param key
	 * @return
	 */
	public Long getExpire(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	/**
	 * @dec 判断key是否存在
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {
		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @dec 删除缓存
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param key
	 * @return
	 */
	public boolean del(String key) {
		try {
			return redisTemplate.delete(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @dec 删除缓存
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param keys
	 * @return
	 */
	public Long dels(Collection<String> keys) {
		try {
			return redisTemplate.delete(keys);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 删除缓存
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param fileIds
	 * @return
	 */
	public Long del(String... fileIds) {
		ArrayList<String> list = CollUtil.toList(fileIds);
		try {
			return redisTemplate.delete(list);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @dec 缓存获取
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return StrUtil.isBlank(key) ? null : redisTemplate.opsForValue().get(key);
	}

}
