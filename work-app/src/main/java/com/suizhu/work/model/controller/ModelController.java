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
import com.suizhu.work.entity.Model;
import com.suizhu.work.model.service.ModelService;

import lombok.AllArgsConstructor;

/**
 * <p>
 * 模版表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
@RestController
@AllArgsConstructor
@RequestMapping("model")
public class ModelController {

	private final ModelService modelService;

	/**
	 * @dec 获取模版
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param categoryId
	 * @return
	 */
	@GetMapping("/{categoryId}")
	public R queryList(@PathVariable("categoryId") String categoryId) {
		QueryWrapper<Model> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("status", Model.STATUS_NORMAL);
		queryWrapper.and(i -> i.eq("category_id", categoryId)).select("id", "name");
		List<Map<String, String>> list = modelService.list(queryWrapper).stream().map(m -> {
			Map<String, String> data = new HashMap<>(2);
			data.put("id", m.getId());
			data.put("name", m.getName());
			return data;
		}).collect(Collectors.toList());

		return R.ok(list);
	}

}
