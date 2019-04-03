package com.suizhu.work.user.service;

import java.util.List;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.UserDoorway;

import cn.hutool.json.JSONObject;

/**
 * <p>
 * 用户表 - 门店表 中间关联表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-11
 */
public interface UserDoorwayService extends IService<UserDoorway> {

	/**
	 * @dec 新增人员
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param list
	 * @param doorwayId
	 * @param json
	 * @return
	 */
	R add(List<UserDoorway> list, String doorwayId, JSONObject json);

	/**
	 * @dec 修改人员
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param userDoorway
	 * @param jo
	 * @return
	 */
	R edit(UserDoorway userDoorway, JSONObject jo);

}
