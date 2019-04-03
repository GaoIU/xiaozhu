package com.suizhu.work.model.service;

import java.util.Map;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.ModelBuild;

/**
 * <p>
 * 工程模版明细 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
public interface ModelBuildService extends IService<ModelBuild> {

	/**
	 * @dec 获取模版明细列表
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	R queryList(Map<String, Object> params);

}
