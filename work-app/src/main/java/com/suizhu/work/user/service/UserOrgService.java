package com.suizhu.work.user.service;

import java.util.List;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.UserOrg;

import cn.hutool.json.JSONObject;

/**
 * <p>
 * 公司人员表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-13
 */
public interface UserOrgService extends IService<UserOrg> {

	/**
	 * @dec 新增公司人员
	 * @date Mar 13, 2019
	 * @author gaochao
	 * @param list
	 * @param userOrg
	 * @param json
	 * @return
	 */
	R add(List<UserOrg> list, UserOrg userOrg, JSONObject json);

	/**
	 * @dec 修改公司人员
	 * @date Mar 13, 2019
	 * @author gaochao
	 * @param userOrg
	 * @param json
	 * @return
	 */
	R edit(UserOrg userOrg, JSONObject json);

	/**
	 * @dec 删除公司人员
	 * @date Mar 13, 2019
	 * @author gaochao
	 * @param id
	 * @param json
	 * @return
	 */
	R del(String id, JSONObject json);

}
