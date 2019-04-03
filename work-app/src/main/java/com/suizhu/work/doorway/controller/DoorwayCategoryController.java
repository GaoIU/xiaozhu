package com.suizhu.work.doorway.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.work.doorway.service.DoorwayCategoryService;
import com.suizhu.work.entity.DoorwayCategory;

import lombok.AllArgsConstructor;

/**
 * <p>
 * 门店分类表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-03
 */
@RestController
@AllArgsConstructor
@RequestMapping("doorwayCategory")
public class DoorwayCategoryController {

	private final DoorwayCategoryService doorwayCategoryService;

	/**
	 * @dec 获取门店分类
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping
	public R queryList() {
		List<Map<String, String>> list = doorwayCategoryService
				.list("status", SqlEmnus.EQ, DoorwayCategory.STATUS_NORMAL, "id", "name").stream().map(c -> {
					Map<String, String> data = new HashMap<>(2);
					data.put("id", c.getId());
					data.put("name", c.getName());
					return data;
				}).collect(Collectors.toList());

		return R.ok(list);
	}

}
