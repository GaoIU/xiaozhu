package com.suizhu.work.build.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suizhu.work.entity.BuildProjectLog;

/**
 * <p>
 * 工程项目记录表 Mapper 接口
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
public interface BuildProjectLogMapper extends BaseMapper<BuildProjectLog> {

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
