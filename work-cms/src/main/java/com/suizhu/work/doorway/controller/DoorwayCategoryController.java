package com.suizhu.work.doorway.controller;

import java.util.ArrayList;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.suizhu.common.core.R;
import com.suizhu.common.exception.MyException;
import com.suizhu.work.doorway.service.DoorwayCategoryService;
import com.suizhu.work.entity.DoorwayCategory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
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
	 * @dec 跳转至门店分类列表页面
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/gotoList")
	public ModelAndView gotoList() {
		return new ModelAndView("doorway/categoryList");
	}

	/**
	 * @dec 获取门店分类列表
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params) {
		return doorwayCategoryService.queryList(params);
	}

	/**
	 * @dec 跳转至门店分类新增或修改页面
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/gotoInfo")
	public ModelAndView gotoInfo(String id) {
		ModelAndView mav = new ModelAndView("doorway/categoryInfo");

		if (StrUtil.isNotBlank(id)) {
			DoorwayCategory doorwayCategory = doorwayCategoryService.getById(id);
			if (doorwayCategory == null) {
				throw new MyException("非法请求！");
			}

			mav.addObject("info", doorwayCategory);
		}

		return mav;
	}

	/**
	 * @dec 新增门店分类
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @param doorwayCategory
	 * @return
	 */
	@PostMapping
	public R save(@Valid @RequestBody DoorwayCategory doorwayCategory) {
		boolean save = doorwayCategoryService.save(doorwayCategory);
		if (save) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改门店分类
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @param doorwayCategory
	 * @return
	 */
	@PutMapping
	public R edit(@Valid @RequestBody DoorwayCategory doorwayCategory) {
		boolean update = doorwayCategoryService.updateById(doorwayCategory);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 批量删除门店分类
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@DeleteMapping
	public R del(String id) {
		if (StrUtil.isBlank(id)) {
			return R.error("无效请求！");
		}

		String[] ids = id.split(",");
		if (ArrayUtil.isEmpty(ids)) {
			return R.error("无效请求！");
		}

		ArrayList<String> idList = CollUtil.toList(ids);
		boolean remove = doorwayCategoryService.removeByIds(idList);
		if (remove) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 启用/禁用门店分类
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @param param
	 * @return
	 */
	@PutMapping("/usable")
	public R usable(@RequestBody Map<String, Object> param) {
		String id = MapUtil.getStr(param, "id");
		Integer status = MapUtil.getInt(param, "status");
		if (StrUtil.isBlank(id) || status == null) {
			return R.error("非法请求！");
		}

		DoorwayCategory doorwayCategory = doorwayCategoryService.getById(id);
		doorwayCategory.setStatus(status);
		if (doorwayCategoryService.updateById(doorwayCategory)) {
			return R.ok();
		}

		return R.error();
	}

}
