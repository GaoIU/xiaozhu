package com.suizhu.batch.controller;

import java.util.ArrayList;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections.MapUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.suizhu.batch.entity.ScheduleJob;
import com.suizhu.batch.service.ScheduleJobService;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.MyException;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 定时任务表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-24
 */
@RestController
@RequestMapping("scheduleJob")
@AllArgsConstructor
public class ScheduleJobController {

	private final ScheduleJobService scheduleJobService;

	/**
	 * @dec 定时任务页面
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/list")
	public ModelAndView gotoList() {
		return new ModelAndView("schedule/list");
	}

	/**
	 * @dec 获取定时任务列表
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params) {
		return scheduleJobService.queryList(params);
	}

	/**
	 * @dec 跳转至定时任务新增或修改页面
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/gotoInfo")
	public ModelAndView gotoInfo(String id) {
		ModelAndView mav = new ModelAndView("schedule/info");

		if (StrUtil.isNotBlank(id)) {
			ScheduleJob scheduleJob = scheduleJobService.getById(id);
			if (scheduleJob == null) {
				throw new MyException("非法请求！");
			}

			mav.addObject("info", scheduleJob);
		}

		return mav;
	}

	/**
	 * @dec 新增定时任务
	 * @date Feb 24, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @return
	 * @throws Exception
	 */
	@PostMapping
	public R save(@Valid @RequestBody ScheduleJob scheduleJob) throws Exception {
		boolean checkJobName = scheduleJobService.exist("job_name", SqlEmnus.EQ, scheduleJob.getJobName());
		if (checkJobName) {
			return R.error("该任务名已存在！");
		}

		return scheduleJobService.saveJob(scheduleJob);
	}

	/**
	 * @dec 修改定时任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param scheduleJob
	 * @return
	 * @throws Exception
	 */
	@PutMapping
	public R edit(@Valid @RequestBody ScheduleJob scheduleJob) throws Exception {
		boolean checkOnly = scheduleJobService.checkOnly(scheduleJob.getId(), "job_name", scheduleJob.getJobName());
		if (!checkOnly) {
			return R.error("该任务名已存在！");
		}

		return scheduleJobService.edit(scheduleJob);
	}

	/**
	 * @dec 删除任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@DeleteMapping
	public R del(String id) throws Exception {
		if (StrUtil.isBlank(id)) {
			return R.error("无效请求！");
		}

		String[] ids = id.split(",");
		if (ArrayUtil.isEmpty(ids)) {
			return R.error("无效请求！");
		}

		ArrayList<String> idList = CollUtil.toList(ids);
		return scheduleJobService.del(idList);
	}

	/**
	 * @dec 开启或者关闭一个定时任务
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/pauseJob")
	public R pauseJob(@RequestBody Map<String, Object> param) throws Exception {
		String id = MapUtils.getString(param, "id");
		Integer status = MapUtils.getInteger(param, "status");
		if (StrUtil.isBlank(id) || status == null) {
			return R.error("非法请求！");
		}

		ScheduleJob scheduleJob = scheduleJobService.getById(id);
		scheduleJob.setStatus(status);

		if (status == ScheduleJob.STATUS_CLOSE) {
			scheduleJobService.pauseJob(scheduleJob);
		}

		if (status == ScheduleJob.STATUS_OPEN) {
			scheduleJobService.resumeJob(scheduleJob);
		}

		boolean update = scheduleJobService.updateById(scheduleJob);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 是否并发
	 * @date Feb 26, 2019
	 * @author gaochao
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/concurrentJob")
	public R concurrentJob(@RequestBody Map<String, Object> param) throws Exception {
		String id = MapUtils.getString(param, "id");
		Integer concurrent = MapUtils.getInteger(param, "concurrent");
		if (StrUtil.isBlank(id) || concurrent == null) {
			return R.error("非法请求！");
		}

		ScheduleJob scheduleJob = scheduleJobService.getById(id);
		scheduleJob.setConcurrent(concurrent);
		scheduleJobService.concurrent(scheduleJob);

		boolean update = scheduleJobService.updateById(scheduleJob);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

}
