package com.suizhu.work.model.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.entity.ModelBuild;
import com.suizhu.work.model.mapper.ModelBuildMapper;
import com.suizhu.work.model.service.ModelBuildService;

/**
 * <p>
 * 工程模版明细 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
@Service
public class ModelBuildServiceImpl extends ServiceImpl<ModelBuildMapper, ModelBuild> implements ModelBuildService {

	/**
	 * @dec 获取最大天数
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.0
	 * @param modelIds
	 * @return
	 */
	@Override
	public int getMaxDays(List<String> modelIds) {
		return baseMapper.getMaxDays(modelIds);
	}

}
