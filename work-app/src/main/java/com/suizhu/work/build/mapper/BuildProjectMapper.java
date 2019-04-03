package com.suizhu.work.build.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suizhu.work.entity.BuildProject;

/**
 * <p>
 * 筹建项目表 Mapper 接口
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
public interface BuildProjectMapper extends BaseMapper<BuildProject> {

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
	 * @dec 获取工序列表
	 * @date Mar 27, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryV2(Map<String, Object> params);

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
