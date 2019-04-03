package com.suizhu.work.mansion.controller;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.ForbiddenException;
import com.suizhu.work.entity.Mansion;
import com.suizhu.work.entity.UserDoorway;
import com.suizhu.work.mansion.service.MansionService;
import com.suizhu.work.user.service.UserDoorwayService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 大楼表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("mansion")
public class MansionController {

	private final MansionService mansionService;

	private final UserDoorwayService userDoorwayService;

	/**
	 * @dec 新增大楼
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param mansion
	 * @param token
	 * @return
	 */
	@PostMapping
	public R save(@Valid Mansion mansion, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
		udqw.eq("doorway_id", mansion.getDoorwayId()).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(udqw);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		mansion.setCreateId(json.getStr("id"));
		boolean save = mansionService.save(mansion);
		if (save) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改大楼
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param mansion
	 * @param token
	 * @return
	 */
	@PutMapping
	public R edit(@Valid Mansion mansion, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
		udqw.eq("doorway_id", mansion.getDoorwayId()).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(udqw);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		boolean update = mansionService.updateById(mansion);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 删除大楼
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@DeleteMapping("/{id}")
	public R del(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
		Mansion mansion = mansionService.getOne("id", SqlEmnus.EQ, id, "id", "doorway_id");
		udqw.eq("doorway_id", mansion.getDoorwayId()).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(udqw);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		return mansionService.del(id);
	}

}
