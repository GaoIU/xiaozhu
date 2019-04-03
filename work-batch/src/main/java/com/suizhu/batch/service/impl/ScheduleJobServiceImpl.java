package com.suizhu.batch.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.MapUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.batch.core.QuartzDceJobLauncher;
import com.suizhu.batch.core.QuartzJobLauncher;
import com.suizhu.batch.entity.ScheduleJob;
import com.suizhu.batch.mapper.ScheduleJobMapper;
import com.suizhu.batch.service.ScheduleJobService;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 定时任务表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-24
 */
@Service
@AllArgsConstructor
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobMapper, ScheduleJob> implements ScheduleJobService {

	private final Scheduler scheduler;

	/**
	 * @dec 初始化数据
	 * @date Feb 24, 2019
	 * @author gaochao
	 */
	@PostConstruct
	@Transactional(rollbackFor = Exception.class)
	public void init() {
		List<ScheduleJob> list = list();
		if (CollUtil.isNotEmpty(list)) {
			list.forEach(sj -> {
				try {
					initJob(sj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	/**
	 * @dec 任务初始化
	 * @date Feb 24, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @throws Exception
	 */
	private void initJob(ScheduleJob scheduleJob) throws Exception {
		String jobName = scheduleJob.getJobName();
		String jobGroup = scheduleJob.getJobGroup();

		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
		CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron())
				.withMisfireHandlingInstructionDoNothing();

		JobDetail jobDetail = null;
		if (ScheduleJob.CONCURRENT_IS == scheduleJob.getConcurrent()) {
			jobDetail = JobBuilder.newJob(QuartzJobLauncher.class).withIdentity(jobKey)
					.withDescription(scheduleJob.getRemark()).build();
		} else if (ScheduleJob.CONCURRENT_NOT == scheduleJob.getConcurrent()) {
			jobDetail = JobBuilder.newJob(QuartzDceJobLauncher.class).withIdentity(jobKey)
					.withDescription(scheduleJob.getRemark()).build();
		}

		if (trigger == null) {
			trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(schedBuilder).build();
			scheduler.scheduleJob(jobDetail, trigger);

			if (ScheduleJob.STATUS_CLOSE == scheduleJob.getStatus()) {
				scheduler.pauseJob(jobKey);
			}
		} else {
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(schedBuilder).build();
			scheduler.rescheduleJob(triggerKey, trigger);
		}
	}

	/**
	 * @dec 新增任务
	 * @date Feb 24, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R saveJob(ScheduleJob scheduleJob) throws Exception {
		initJob(scheduleJob);
		boolean save = save(scheduleJob);
		if (save) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 获取定时任务列表
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public R queryList(Map<String, Object> params) {
		Integer current = MapUtils.getInteger(params, "current", 1);
		Integer size = MapUtils.getInteger(params, "size", 15);
		String jobName = MapUtils.getString(params, "jobName");
		String jobGroup = MapUtils.getString(params, "jobGroup");
		Short status = MapUtils.getShort(params, "status");
		String beginTime = MapUtils.getString(params, "beginTime");
		String endTime = MapUtils.getString(params, "endTime");
		QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("create_time");

		if (StrUtil.isNotBlank(jobName)) {
			queryWrapper.and(i -> i.like("job_name", jobName));
		}
		if (StrUtil.isNotBlank(jobGroup)) {
			queryWrapper.and(i -> i.like("job_group", jobGroup));
		}
		if (status != null) {
			queryWrapper.and(i -> i.eq("status", status));
		}
		if (StrUtil.isNotBlank(beginTime)) {
			queryWrapper.and(i -> i.ge("create_time", beginTime));
		}
		if (StrUtil.isNotBlank(endTime)) {
			queryWrapper.and(i -> i.le("create_time", endTime + " 23:59:59"));
		}

		IPage<ScheduleJob> page = new Page<>(current, size);
		page = page(page, queryWrapper);
		return R.ok(page);
	}

	/**
	 * @dec 修改任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @return
	 * @throws Exception
	 */
	@Override
	public R edit(ScheduleJob scheduleJob) throws Exception {
		boolean update = updateById(scheduleJob);
		if (update) {
			TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
			scheduler.pauseTrigger(triggerKey);
			scheduler.unscheduleJob(triggerKey);
			JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
			scheduler.deleteJob(jobKey);
			initJob(scheduleJob);

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 删除任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param idList
	 * @return
	 * @throws Exception
	 */
	@Override
	public R del(List<String> idList) throws Exception {
		int i = 0;
		for (String id : idList) {
			ScheduleJob scheduleJob = getById(id);
			TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
			scheduler.pauseTrigger(triggerKey);
			scheduler.unscheduleJob(triggerKey);
			JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
			scheduler.deleteJob(jobKey);

			boolean remove = removeById(id);
			if (!remove) {
				i++;
			}
		}

		if (i == 0) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 关闭定时任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @throws Exception
	 */
	@Override
	public void pauseJob(ScheduleJob scheduleJob) throws Exception {
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		scheduler.pauseJob(jobKey);
	}

	/**
	 * @dec 开启定时任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @throws Exception
	 */
	@Override
	public void resumeJob(ScheduleJob scheduleJob) throws Exception {
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		scheduler.resumeJob(jobKey);
	}

	/**
	 * @dec 是否并发任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @throws Exception
	 */
	@Override
	public void concurrent(ScheduleJob scheduleJob) throws Exception {
		TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		scheduler.pauseTrigger(triggerKey);
		scheduler.unscheduleJob(triggerKey);
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		scheduler.deleteJob(jobKey);
		initJob(scheduleJob);
	}

}
