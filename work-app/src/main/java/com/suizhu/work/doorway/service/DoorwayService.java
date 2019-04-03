package com.suizhu.work.doorway.service;

import java.util.Map;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.condition.build.BuildCondition;
import com.suizhu.work.entity.Doorway;
import com.suizhu.work.entity.UserOrg;

import cn.hutool.json.JSONObject;

/**
 * <p>
 * 门店表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
public interface DoorwayService extends IService<Doorway> {

	/**
	 * @dec 获取门店列表
	 * @date Mar 4, 2019
	 * @author gaochao
	 * @param params
	 * @param json
	 * @param userOrg
	 * @return
	 */
	R queryList(Map<String, Object> params, JSONObject json, UserOrg userOrg);

	/**
	 * @dec 新增门店
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param doorway
	 * @return
	 */
	R addDoorway(Doorway doorway);

	/**
	 * @dec 获取门店工程项目信息
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param id
	 * @param categoryId
	 * @param type
	 * @param json
	 * @param userOrg
	 * @return
	 */
	R getInfo(String id, String categoryId, Integer type, JSONObject json, UserOrg userOrg);

	/**
	 * @dec 获取日历
	 * @date Mar 10, 2019
	 * @author gaochao
	 * @param id
	 * @param categoryId
	 * @param year
	 * @param month
	 * @return
	 */
	R getCalendar(String id, String categoryId, Integer year, Integer month);

	/**
	 * @dec 查看门店概况
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param id
	 * @param json
	 * @param count
	 * @return
	 */
	R detail(String id, JSONObject json, int count);

	/**
	 * @dec 获取图片列表
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param id
	 * @param type
	 * @param current
	 * @param size
	 * @return
	 */
	R fileImages(String id, Integer type, Integer current, Integer size);

	/**
	 * @dec 保存文件
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param uploadFileId
	 * @param delFileId
	 * @param type
	 * @return
	 */
	R updateFile(String uploadFileId, String delFileId, Integer type);

	/**
	 * @dec 修改门店
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param doorway
	 * @param json
	 * @param userOrg
	 * @return
	 */
	R edit(Doorway doorway, JSONObject json, UserOrg userOrg);

	/**
	 * @dec 查看楼层列表
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	R mansion(String id, String token);

	/**
	 * @dec 获取人员管理列表
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param id
	 * @param dep
	 * @param workKind
	 * @param token
	 * @return
	 */
	R user(String id, String dep, String workKind, String token);

	/**
	 * @dec 获取人员管理选项卡
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	R userOption(String id, String token);

	/**
	 * @dec 获取人员管理列表
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param condition
	 * @param userDoorwayId
	 * @param token
	 * @return
	 */
	R userV2(String id, String condition, String userDoorwayId, String token);

	/**
	 * @dec 获取门店工程项目信息
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.1.0
	 * @param buildCondition
	 * @param json
	 * @param userOrg
	 * @return
	 */
	R info(BuildCondition buildCondition, JSONObject json, UserOrg userOrg);

	/**
	 * @dec 获取门店概况
	 * @date Mar 28, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param json
	 * @param count
	 * @return
	 */
	R detailV2(String id, JSONObject json, int count);

	/**
	 * @dec 获取图片列表
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param condition
	 * @param current
	 * @param size
	 * @return
	 */
	R fileImages(String id, String condition, Integer current, Integer size);

}
