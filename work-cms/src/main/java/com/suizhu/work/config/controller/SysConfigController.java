package com.suizhu.work.config.controller;

import java.util.ArrayList;
import java.util.List;
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

import com.suizhu.cms.codegan.CodeGan;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.MyException;
import com.suizhu.work.config.service.SysConfigService;
import com.suizhu.work.entity.SysConfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 系统配置表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("sysConfig")
public class SysConfigController {

	private final SysConfigService sysConfigService;

	/**
	 * @dec 跳转至后台系统配置列表页面
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/gotoList")
	public ModelAndView gotoList() {
		return new ModelAndView("config/list");
	}

	/**
	 * @dec 获取后台系统配置列表
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params) {
		return sysConfigService.queryList(params);
	}

	/**
	 * @dec 跳转至后台系统配置新增或修改页面
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/gotoInfo")
	public ModelAndView gotoInfo(String id) {
		ModelAndView mav = new ModelAndView("config/info");
		if (StrUtil.isNotBlank(id)) {
			SysConfig sysConfig = sysConfigService.getById(id);
			if (sysConfig == null) {
				throw new MyException("非法请求！");
			}

			mav.addObject("info", sysConfig);
		}

		return mav;
	}

	/**
	 * @dec 验证后台系统配置编码是否存在
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param code
	 * @param sysConfigId
	 * @return
	 */
	@PostMapping("/checkCode")
	public R checkCode(String code, String sysConfigId) {
		boolean checkOnly = sysConfigService.checkOnly(sysConfigId, "name", code);
		return R.ok(!checkOnly);
	}

	/**
	 * @dec 添加后台系统配置
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param sysConfig
	 * @return
	 */
	@PostMapping
	public R save(@Valid @RequestBody SysConfig sysConfig) {
		boolean exist = sysConfigService.exist("name", SqlEmnus.EQ, sysConfig.getName());
		if (exist) {
			return R.error("该配置编码已被使用！");
		}

		boolean save = sysConfigService.save(sysConfig);
		if (save) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改后台系统配置
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param sysConfig
	 * @return
	 */
	@PutMapping
	public R edit(@Valid @RequestBody SysConfig sysConfig) {
		boolean checkOnly = sysConfigService.checkOnly(sysConfig.getId(), "name", sysConfig.getName());
		if (!checkOnly) {
			return R.error("该配置编码已被使用！");
		}

		boolean update = sysConfigService.updateById(sysConfig);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 批量删除后台系统配置
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@DeleteMapping
	public R del(String id) {
		if (StrUtil.isBlank(id)) {
			return R.error("非法请求！");
		}
		String[] ids = id.split(",");
		if (ArrayUtil.isEmpty(ids)) {
			return R.error("非法请求！");
		}

		ArrayList<String> idList = CollUtil.toList(ids);
		boolean remove = sysConfigService.removeByIds(idList);
		if (remove) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 启用或者禁用后台系统配置
	 * @date Feb 22, 2019
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

		SysConfig sysConfig = sysConfigService.getById(id);
		sysConfig.setStatus(status);
		boolean update = sysConfigService.updateById(sysConfig);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 跳转至代码生成页面
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/codeGan")
	public ModelAndView gotoCodeGan() {
		ModelAndView mav = new ModelAndView("config/codeGan");
		List<String> tables = sysConfigService.queryTables();
		mav.addObject("tables", tables);
		return mav;
	}

	/**
	 * @dec 生成代码
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param codeGan
	 * @return
	 */
	@PostMapping("/codeGan")
	public R codeGan(@Valid @RequestBody CodeGan codeGan) {
		boolean gan = sysConfigService.codeGan(codeGan);
		if (gan) {
			return R.ok();
		}

		return R.error();
	}

}
