package com.suizhu.work.user.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.MyException;
import com.suizhu.work.entity.Organization;
import com.suizhu.work.user.service.OrganizationService;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 公司组织表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-28
 */
@RestController
@AllArgsConstructor
@RequestMapping("organization")
public class OrganizationController {

	private final OrganizationService organizationService;

	/**
	 * @dec 跳转至公司列表页面
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/gotoList")
	public ModelAndView gotoList() {
		return new ModelAndView("user/orglist");
	}

	/**
	 * @dec 查询公司列表
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params) {
		return organizationService.queryList(params);
	}

	/**
	 * @dec 跳转至公司新增或修改页面
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/gotoInfo")
	public ModelAndView gotoInfo(String id) {
		ModelAndView mav = new ModelAndView("user/orginfo");

		if (StrUtil.isNotBlank(id)) {
			Organization organization = organizationService.getById(id);
			if (organization == null) {
				throw new MyException("非法请求！");
			}

			mav.addObject("info", organization);
		}

		return mav;
	}

	/**
	 * @dec 验证公司名称
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param id
	 * @param name
	 * @return
	 */
	@PostMapping("/checkName")
	public R checkName(String id, String name) {
		boolean checkOnly = organizationService.checkOnly(id, "name", name);
		return R.ok(!checkOnly);
	}

	/**
	 * @dec 新增公司
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param organization
	 * @return
	 */
	@PostMapping
	public R save(@Valid @RequestBody Organization organization) {
		boolean exist = organizationService.exist("name", SqlEmnus.EQ, organization.getName());
		if (exist) {
			return R.error("该名称已存在！");
		}

		boolean save = organizationService.save(organization);
		if (save) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改公司
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param organization
	 * @return
	 */
	@PutMapping
	public R edit(@Valid @RequestBody Organization organization) {
		boolean checkOnly = organizationService.checkOnly(organization.getId(), "name", organization.getName());
		if (!checkOnly) {
			return R.error("该名称已存在！");
		}

		boolean update = organizationService.updateById(organization);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

}
