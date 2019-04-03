package com.suizhu.work.model.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.core.R;
import com.suizhu.work.entity.ModelBuild;
import com.suizhu.work.model.service.ModelBuildService;

import lombok.AllArgsConstructor;

/**
 * <p>
 * 工程模版明细 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
@RestController
@AllArgsConstructor
@RequestMapping("modelBuild")
public class ModelBuildController {

	private final ModelBuildService modelBuildService;

	/**
	 * @dec 获取模版明细
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param modelId
	 * @return
	 */
	@GetMapping("/{modelId}")
	public R queryList(@PathVariable("modelId") String modelId) {
		QueryWrapper<ModelBuild> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("model_id", modelId).orderByAsc("sort");
		queryWrapper.select("sort", "name", "begin_day", "days");
		List<Map<String, Object>> list = modelBuildService.list(queryWrapper).stream().map(mb -> {
			Map<String, Object> data = new HashMap<>(4);
			data.put("sort", mb.getSort());
			data.put("name", mb.getName());
			data.put("beginDay", mb.getBeginDay());
			data.put("days", mb.getDays());
			return data;
		}).collect(Collectors.toList());

		return R.ok(list);
	}

}
