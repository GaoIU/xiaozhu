package com.suizhu.work.build.service;

import java.util.List;
import java.util.Map;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildProjectLog;

import cn.hutool.json.JSONObject;

/**
 * <p>
 * 筹建项目表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
public interface BuildProjectService extends IService<BuildProject> {

	/**
	 * @dec 获取项目列表
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param buildEnginerId
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryList(Map<String, Object> params);

	/**
	 * @dec 获取下一个项目列表
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> nextQuery(Map<String, Object> params);

	/**
	 * @dec 获取条数
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	int queryCount(Map<String, Object> params);

	/**
	 * @dec 新增工序
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildProject
	 * @param decodeToken
	 * @return
	 */
	R add(BuildProject buildProject, JSONObject decodeToken);

	/**
	 * @dec 删除工序
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildProject
	 * @param json
	 * @return
	 */
	R del(BuildProject buildProject, JSONObject json);

	/**
	 * @dec 获取项目日志
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param userId
	 * @return
	 */
	List<Map<String, Object>> info(String id, String userId);

	/**
	 * @dec 分配组长
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param userId
	 * @return
	 */
	R setSernice(String id, String userId);

	/**
	 * @dec 根据月份查询数据
	 * @date Mar 10, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> findByMonth(Map<String, String> params);

	/**
	 * @dec 获取月份选项卡
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param buildEnginerId
	 * @return
	 */
	List<Integer> option(String buildEnginerId);

	/**
	 * @dec 查看项目
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param params
	 * @param token
	 * @return
	 */
	List<Map<String, Object>> view(Map<String, Object> params, String token);

	/**
	 * @dec 过滤数据
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param bpls
	 * @return
	 */
	List<Map<String, Object>> filterData(List<BuildProjectLog> bpls);

	/**
	 * @dec 获取工序列表
	 * @date Mar 27, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryV2(Map<String, Object> params);

	/**
	 * @dec 删除工序组长
	 * @date Mar 28, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param userDoorwayId
	 * @return
	 */
	R delSernice(String id, String userDoorwayId);

	/**
	 * @dec 获取日历数据
	 * @date Apr 1, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryCalendar(Map<String, Object> params);

}
