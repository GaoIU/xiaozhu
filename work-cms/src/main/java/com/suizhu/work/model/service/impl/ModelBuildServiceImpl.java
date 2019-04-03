package com.suizhu.work.model.service.impl;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.entity.ModelBuild;
import com.suizhu.work.model.mapper.ModelBuildMapper;
import com.suizhu.work.model.service.ModelBuildService;

import cn.hutool.core.util.StrUtil;

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
	 * @dec 获取模版明细列表
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public R queryList(Map<String, Object> params) {
		Integer current = MapUtils.getInteger(params, "current", 1);
		Integer size = MapUtils.getInteger(params, "size", 15);
		String modelId = MapUtils.getString(params, "modelId");
		String name = MapUtils.getString(params, "name");
		QueryWrapper<ModelBuild> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByAsc("sort");

		if (StrUtil.isNotBlank(modelId)) {
			queryWrapper.and(i -> i.eq("model_id", modelId));
		}

		if (StrUtil.isNotBlank(name)) {
			queryWrapper.and(i -> i.like("name", name));
		}

		IPage<ModelBuild> page = new Page<>(current, size);
		page = page(page, queryWrapper);
		return R.ok(page);
	}

}
