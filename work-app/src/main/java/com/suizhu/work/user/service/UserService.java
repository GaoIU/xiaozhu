package com.suizhu.work.user.service;

import org.springframework.web.multipart.MultipartFile;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-28
 */
public interface UserService extends IService<User> {

	/**
	 * @dec 账号密码登录
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param username
	 * @param password
	 * @param clientId
	 * @return
	 */
	R signIn(String username, String password, String clientId);

	/**
	 * @dec Token登录
	 * @date Mar 1, 2019
	 * @author gaochao
	 * @param clientId
	 * @param token
	 * @return
	 */
	R signToken(String clientId, String token);

	/**
	 * @dec 发送短信验证码
	 * @date Mar 2, 2019
	 * @author gaochao
	 * @param username
	 * @return
	 */
	R sendSmsCode(String username);

	/**
	 * @dec 根据短信验证码重置密码
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @param username
	 * @param password
	 * @param code
	 * @return
	 */
	R restPwd(String username, String password, String code);

	/**
	 * @dec 发送短信
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param type
	 * @param mobile
	 * @param content
	 */
	void sendSms(Integer type, String mobile, String content);

	/**
	 * @dec 上传头像
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param token
	 * @param file
	 * @return
	 */
	R upload(String token, MultipartFile file);

}
