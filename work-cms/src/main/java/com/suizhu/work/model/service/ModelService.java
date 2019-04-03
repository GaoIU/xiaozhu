package com.suizhu.work.model.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.Model;

/**
 * <p>
 * 模版表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
public interface ModelService extends IService<Model> {

	/**
	 * @dec 获取模版列表
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	R queryList(Map<String, Object> params);

	/**
	 * @dec 新增模版
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param model
	 * @param file
	 * @return
	 * @throws IOException
	 */
	R addModel(Model model, MultipartFile file) throws IOException;

	/**
	 * @dec 修改模版
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @param model
	 * @param file
	 * @return
	 * @throws IOException
	 */
	R editModel(Model model, MultipartFile file) throws IOException;

}
