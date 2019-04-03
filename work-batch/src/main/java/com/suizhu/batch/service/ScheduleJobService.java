package com.suizhu.batch.service;

import java.util.List;
import java.util.Map;

import com.suizhu.batch.entity.ScheduleJob;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;

/**
 * <p>
 * 定时任务表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-24
 */
public interface ScheduleJobService extends IService<ScheduleJob> {

	/**
	 * @dec 新增任务
	 * @date Feb 24, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @return
	 * @throws Exception
	 */
	R saveJob(ScheduleJob scheduleJob) throws Exception;

	/**
	 * @dec 获取定时任务列表
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	R queryList(Map<String, Object> params);

	/**
	 * @dec 修改任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @return
	 * @throws Exception
	 */
	R edit(ScheduleJob scheduleJob) throws Exception;

	/**
	 * @dec 删除任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param idList
	 * @return
	 * @throws Exception
	 */
	R del(List<String> idList) throws Exception;

	/**
	 * @dec 关闭定时任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @throws Exception
	 */
	void pauseJob(ScheduleJob scheduleJob) throws Exception;

	/**
	 * @dec 开启定时任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @throws Exception
	 */
	void resumeJob(ScheduleJob scheduleJob) throws Exception;

	/**
	 * @dec 是否并发任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @throws Exception
	 */
	void concurrent(ScheduleJob scheduleJob) throws Exception;

}
