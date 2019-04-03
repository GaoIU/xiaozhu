package com.suizhu.cms.controller;

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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.cms.entity.SysResource;
import com.suizhu.cms.service.SysResourceService;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.MyException;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 后台资源表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("sysResource")
public class SysResourceController {

	private final SysResourceService sysResourceService;

	/**
	 * @dec 跳转至后台资源列表页面
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/gotoList")
	public ModelAndView gotoList() {
		return new ModelAndView("sys/resource/list");
	}

	/**
	 * @dec 获取后台资源列表
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params) {
		return sysResourceService.queryList(params);
	}

	/**
	 * @dec 跳转至后台资源新增或修改页面
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/gotoInfo")
	public ModelAndView gotoInfo(String id) {
		ModelAndView mav = new ModelAndView("sys/resource/info");

		if (StrUtil.isNotBlank(id)) {
			boolean exist = sysResourceService.exist("id", SqlEmnus.EQ, id);
			if (!exist) {
				throw new MyException("非法请求！");
			}

			mav.addObject("info", sysResourceService.view(id));
		}

		return mav;
	}

	/**
	 * @dec 查看资源树菜单
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/view")
	public R view() {
		QueryWrapper<SysResource> wrapper = new QueryWrapper<>();
		wrapper.eq("status", SysResource.STATUS_NORMAL);
		wrapper.and(i -> i.eq("type", SysResource.TYPE_MENU));
		wrapper.and(i -> i.isNull("parent_id").or().eq("parent_id", ""));
		wrapper.select("id", "name", "icon", "type");
		wrapper.orderByAsc("sort");
		wrapper.orderByDesc("create_time");
		List<SysResource> list = sysResourceService.list(wrapper);

		return R.ok(sysResourceService.getMenu(list, "children", null));
	}

	/**
	 * @dec 验证后台资源编码是否存在
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param code
	 * @param sysResourceId
	 * @return
	 */
	@PostMapping("/checkCode")
	public R checkCode(String code, String sysResourceId) {
		boolean checkOnly = sysResourceService.checkOnly(sysResourceId, "code", code.toUpperCase());
		return R.ok(!checkOnly);
	}

	/**
	 * @dec 添加后台资源
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysResource
	 * @return
	 */
	@PostMapping
	public R save(@Valid @RequestBody SysResource sysResource) {
		String code = sysResource.getCode().toUpperCase();
		boolean checkCode = sysResourceService.exist("code", SqlEmnus.EQ, code);
		if (checkCode) {
			return R.error("该资源编码已被使用！");
		}

		String method = sysResource.getMethod();
		sysResource.setCode(code);
		sysResource.setMethod(method.toUpperCase());

		boolean save = sysResourceService.save(sysResource);
		if (save) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改后台资源
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysResource
	 * @return
	 */
	@PutMapping
	public R edit(@Valid @RequestBody SysResource sysResource) {
		String code = sysResource.getCode();
		boolean checkCode = sysResourceService.checkOnly(sysResource.getId(), "code", code.toUpperCase());
		if (!checkCode) {
			return R.error("该资源编码已被使用！");
		}

		String method = sysResource.getMethod();
		sysResource.setCode(code.toUpperCase());
		sysResource.setMethod(method.toUpperCase());

		boolean update = sysResourceService.updateById(sysResource);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 批量删除后台资源
	 * @date Feb 21, 2019
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
		boolean remove = sysResourceService.removeByIds(idList);
		if (remove) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 启用或者禁用后台资源
	 * @date Feb 21, 2019
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

		SysResource sysResource = sysResourceService.getById(id);
		sysResource.setStatus(status);
		if (sysResourceService.updateById(sysResource)) {
			return R.ok();
		}

		return R.error();
	}

}
