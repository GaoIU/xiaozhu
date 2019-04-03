package com.suizhu.work.model.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.MyException;
import com.suizhu.work.doorway.service.DoorwayCategoryService;
import com.suizhu.work.entity.DoorwayCategory;
import com.suizhu.work.entity.Model;
import com.suizhu.work.model.service.ModelService;

import cn.hutool.core.util.StrUtil;
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

	private final DoorwayCategoryService doorwayCategoryService;

	/**
	 * @dec 跳转至模版列表页面
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @return
	 */
	@GetMapping("/gotoList")
	public ModelAndView gotoList() {
		return new ModelAndView("model/list");
	}

	/**
	 * @dec 获取模版列表
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@GetMapping
	public R queryList(@RequestParam Map<String, Object> params) {
		return modelService.queryList(params);
	}

	/**
	 * @dec 跳转至模版新增或修改页面
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@GetMapping("/gotoInfo")
	public ModelAndView gotoInfo(String id) {
		ModelAndView mav = new ModelAndView("/model/info");
		List<DoorwayCategory> list = doorwayCategoryService.list("status", SqlEmnus.EQ, DoorwayCategory.STATUS_NORMAL,
				"id", "name");
		mav.addObject("categorys", list);

		if (StrUtil.isNotBlank(id)) {
			Model model = modelService.getById(id);
			if (model == null) {
				throw new MyException("非法请求！");
			}

			mav.addObject("info", model);
		}
		return mav;
	}

	/**
	 * @dec 新增模版
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param model
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@PostMapping
	public R save(@Valid Model model, MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			return R.error("请选择上传模版！");
		}
		return modelService.addModel(model, file);
	}

	/**
	 * @dec 修改模版
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @param model
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@PutMapping
	public R edit(@Valid Model model, MultipartFile file) throws IOException {
		return modelService.editModel(model, file);
	}

}
