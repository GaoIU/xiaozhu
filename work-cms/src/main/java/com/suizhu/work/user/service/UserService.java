package com.suizhu.work.user.service;

import java.util.Map;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-13
 */
public interface UserService extends IService<User> {

	/**
	 * @dec 获取用户列表
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	R queryList(Map<String, Object> params);

	/**
	 * @dec 新增用户
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param user
	 * @return
	 */
	R addUser(User user);

	/**
	 * @dec 禁用或启用用户
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param id
	 * @param status
	 * @return
	 */
	R usable(String id, Integer status);

}
