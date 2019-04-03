package com.suizhu.work.mansion.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suizhu.common.core.R;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.work.entity.Floor;
import com.suizhu.work.mansion.service.FloorService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 楼层表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("floor")
public class FloorController {

	private final FloorService floorService;

	/**
	 * @dec 新增楼层
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param floor
	 * @param token
	 * @return
	 */
	@PostMapping
	public R save(@Valid Floor floor, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		String[] names = floor.getName().split(",");
		List<Floor> floors = new ArrayList<>(names.length);
		for (String name : names) {
			if (name.trim().length() <= 0 || name.trim().length() > 64) {
				throw new BadRequestException("楼层名称长度不能超过64位！");
			}
			Floor f = new Floor(name, floor.getMansionId(), json.getStr("id"));
			floors.add(f);
		}
		boolean save = floorService.saveBatch(floors);
		if (save) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改楼层
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param floor
	 * @param token
	 * @return
	 */
	@PutMapping
	public R edit(@Valid Floor floor) {
		boolean update = floorService.updateById(floor);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 删除楼层
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@DeleteMapping("/{id}")
	public R del(@PathVariable String id) {
		return floorService.del(id);
	}

}
