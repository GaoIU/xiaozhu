package com.suizhu.cms.service;

import java.util.ArrayList;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import com.suizhu.cms.entity.SysUser;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;

/**
 * <p>
 * 后台用户表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
public interface SysUserService extends IService<SysUser> {

	/**
	 * @dec 上传后台用户头像
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param avatar
	 * @param sysUserId
	 * @return
	 */
	R uploadAvatar(MultipartFile avatar, String sysUserId);

	/**
	 * @dec 获取后台用户列表
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	R queryList(Map<String, Object> params);

	/**
	 * @dec 添加后台用户
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysUser
	 * @return
	 */
	R add(@Valid SysUser sysUser);

	/**
	 * @dec 修改后台用户
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysUser
	 * @return
	 */
	R edit(@Valid SysUser sysUser);

	/**
	 * @dec 删除后台用户
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param idList
	 * @return
	 */
	R del(ArrayList<String> idList);

}
