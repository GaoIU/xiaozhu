package com.suizhu.work.doorway.controller;

import java.time.LocalDate;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.common.exception.ForbiddenException;
import com.suizhu.work.condition.build.BuildCondition;
import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.Doorway;
import com.suizhu.work.entity.Organization;
import com.suizhu.work.entity.UserOrg;
import com.suizhu.work.user.service.OrganizationService;
import com.suizhu.work.user.service.UserOrgService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 门店表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("doorway")
public class DoorwayController {

	private final DoorwayService doorwayService;

	private final UserOrgService userOrgService;

	private final OrganizationService organizationService;

	/**
	 * @dec 获取门店列表
	 * @date Mar 4, 2019
	 * @author gaochao
	 * @param params
	 * @param token
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");

		if (userOrg != null) {
			Organization organization = organizationService.getById(userOrg.getOrgId());
			if (organization.getExpireTime().isBefore(LocalDate.now())) {
				throw new ForbiddenException("公司已到期！");
			}
		}

		return doorwayService.queryList(params, json, userOrg);
	}

	/**
	 * @dec 新增门店
	 * @date Mar 4, 2019
	 * @author gaochao
	 * @param doorway
	 * @param token
	 * @return
	 */
	@PostMapping
	public R save(@Valid Doorway doorway, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		if (userOrg == null) {
			throw new ForbiddenException();
		}

		Organization organization = organizationService.getById(userOrg.getOrgId());
		if (organization.getExpireTime().isBefore(LocalDate.now())) {
			throw new ForbiddenException("公司已到期！");
		}

		int count = doorwayService.count("org_id", SqlEmnus.EQ, userOrg.getOrgId());
		if (organization.getDoorwayNum() <= count) {
			throw new ForbiddenException("公司门店数已达上限！");
		}

		doorway.setOrgId(userOrg.getOrgId());
		doorway.setCreateId(json.getStr("id"));
		return doorwayService.addDoorway(doorway);
	}

	/**
	 * @dec 获取门店项目
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param id
	 * @param categoryId
	 * @param type
	 * @return
	 */
	@GetMapping("/info")
	public R getInfo(String id, String categoryId, Integer type,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		if (StrUtil.isBlank(id) || StrUtil.isBlank(categoryId) || type == null) {
			throw new BadRequestException();
		}

		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "id", "org_id");

		return doorwayService.getInfo(id, categoryId, type, json, userOrg);
	}

	/**
	 * @dec 获取日历
	 * @date Mar 10, 2019
	 * @author gaochao
	 * @param id
	 * @param categoryId
	 * @param year
	 * @param month
	 * @return
	 */
	@GetMapping("/calendar")
	public R getCalendar(String id, String categoryId, Integer year, Integer month) {
		if (StrUtil.isBlank(id) || StrUtil.isBlank(categoryId) || year == null || month == null || month <= 0
				|| year <= 0) {
			throw new BadRequestException();
		}

		return doorwayService.getCalendar(id, categoryId, year, month);
	}

	/**
	 * @dec 查看门店概况
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public R detail(@PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		int count = userOrgService.count("user_id", SqlEmnus.EQ, json.getStr("id"));

		return doorwayService.detail(id, json, count);
	}

	/**
	 * @dec 获取门店图片
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param id
	 * @param current
	 * @param size
	 * @return
	 */
	@GetMapping("/{id}/files")
	public R fileImages(@PathVariable String id, @RequestParam(defaultValue = "0") Integer type,
			@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "9") Integer size) {
		return doorwayService.fileImages(id, type, current, size);
	}

	/**
	 * @dec 保存文件
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param uploadFileId
	 * @param delFileId
	 * @param type
	 * @return
	 */
	@PutMapping("/updateFile")
	public R updateFile(String uploadFileId, String delFileId, Integer type) {
		if (type == null) {
			throw new BadRequestException();
		}

		if (StrUtil.isBlank(uploadFileId)) {
			if (StrUtil.isBlank(delFileId)) {
				throw new BadRequestException();
			}
		}

		if (StrUtil.isBlank(delFileId)) {
			if (StrUtil.isBlank(uploadFileId)) {
				throw new BadRequestException();
			}
		}

		return doorwayService.updateFile(uploadFileId, delFileId, type);
	}

	/**
	 * @dec 修改门店
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param doorway
	 * @param token
	 * @return
	 */
	@PutMapping
	public R edit(@Valid Doorway doorway, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "org_id");
		return doorwayService.edit(doorway, json, userOrg);
	}

	/**
	 * @dec 获取楼层列表
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@GetMapping("/{id}/mansion")
	public R mansion(@PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		return doorwayService.mansion(id, token);
	}

	/**
	 * @dec 获取人员管理列表
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@GetMapping("/user")
	public R user(String id, String dep, String workKind, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		return doorwayService.user(id, dep, workKind, token);
	}

	/**
	 * @dec 获取人员管理列表
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param condition
	 * @param token
	 * @return
	 */
	@GetMapping("/user/v2")
	public R userDoorway(String id, String condition, String userDoorwayId,
			@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		return doorwayService.userV2(id, condition, userDoorwayId, token);
	}

	/**
	 * @dec 获取人员管理列表选项卡
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@GetMapping("/{id}/user")
	public R userOption(@PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		return doorwayService.userOption(id, token);
	}

	/**
	 * @dec 设置封面
	 * @date Mar 15, 2019
	 * @author gaochao
	 * @param id
	 * @param fileUrl
	 * @return
	 */
	@PostMapping("/cover")
	public R cover(String id, String fileUrl) {
		Doorway doorway = doorwayService.getOne("id", SqlEmnus.EQ, id, "id", "cover");
		doorway.setCover(fileUrl);
		boolean update = doorwayService.updateById(doorway);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 获取门店项目
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.1.0
	 * @param buildCondition
	 * @param token
	 * @return
	 */
	@GetMapping("/info/v2")
	public R info(@Valid BuildCondition buildCondition, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		UserOrg userOrg = userOrgService.getOne("user_id", SqlEmnus.EQ, json.getStr("id"), "id", "org_id");
		return doorwayService.info(buildCondition, json, userOrg);
	}

	/**
	 * @dec 获取门店概况
	 * @date Mar 28, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param token
	 * @return
	 */
	@GetMapping("/{id}/v2")
	public R detailV2(@PathVariable("id") String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		int count = userOrgService.count("user_id", SqlEmnus.EQ, json.getStr("id"));

		return doorwayService.detailV2(id, json, count);
	}

	/**
	 * @dec 获取图片列表
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param condition
	 * @param current
	 * @param size
	 * @return
	 */
	@GetMapping("/{id}/files/v2")
	public R fileImages(@PathVariable String id, String condition, @RequestParam(defaultValue = "1") Integer current,
			@RequestParam(defaultValue = "9") Integer size) {
		return doorwayService.fileImages(id, condition, current, size);
	}

}
