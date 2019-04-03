package com.suizhu.work.build.service;

import java.util.List;
import java.util.Map;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.BuildProjectLog;

/**
 * <p>
 * 工程项目记录表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
public interface BuildProjectLogService extends IService<BuildProjectLog> {

	/**
	 * @dec 新增项目日志
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param buildProjectLog
	 * @param doorwayId
	 * @return
	 */
	boolean add(BuildProjectLog buildProjectLog, String doorwayId);

	/**
	 * @dec 删除日志
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	R del(String id);

	/**
	 * @dec 获取周报日期
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> weekTime(Map<String, Object> params);

	/**
	 * @dec 获取月份选项卡
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @return
	 */
	List<Integer> weekMonth();

}
