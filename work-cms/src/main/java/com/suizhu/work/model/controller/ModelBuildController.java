package com.suizhu.work.model.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.MyException;
import com.suizhu.work.entity.Model;
import com.suizhu.work.entity.ModelBuild;
import com.suizhu.work.model.service.ModelBuildService;
import com.suizhu.work.model.service.ModelService;

import cn.hutool.core.util.StrUtil;
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

	private final ModelService modelService;

	/**
	 * @dec 跳转至模版明细列表页面
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/gotoList")
	public ModelAndView gotoList() {
		ModelAndView mav = new ModelAndView("model/detaillist");
		List<Model> list = modelService.list("status", SqlEmnus.EQ, Model.STATUS_NORMAL, "id", "name");
		mav.addObject("models", list);
		return mav;
	}

	/**
	 * @dec 获取模版明细列表
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params) {
		return modelBuildService.queryList(params);
	}

	/**
	 * @dec 跳转至新增或者修改页面
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/gotoInfo")
	public ModelAndView gotoInfo(String id) {
		ModelAndView mav = new ModelAndView("/model/detailinfo");
		if (StrUtil.isNotBlank(id)) {
			ModelBuild modelBuild = modelBuildService.getById(id);
			if (modelBuild == null) {
				throw new MyException("非法请求！");
			}

			mav.addObject("info", modelBuild);
		}
		return mav;
	}

	/**
	 * @dec 修改模版明细
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @param modelBuild
	 * @return
	 */
	@PutMapping
	public R edit(@RequestBody ModelBuild modelBuild) {
		boolean update = modelBuildService.updateById(modelBuild);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

}
