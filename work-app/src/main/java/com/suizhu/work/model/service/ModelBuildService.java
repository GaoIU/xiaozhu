package com.suizhu.work.model.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

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
	 * @dec 获取最大天数
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.0
	 * @param modelIds
	 * @return
	 */
	int getMaxDays(@Param("modelIds") List<String> modelIds);

}
