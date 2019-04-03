package com.suizhu.work.doorway.service.impl;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.doorway.mapper.DoorwayCategoryMapper;
import com.suizhu.work.doorway.service.DoorwayCategoryService;
import com.suizhu.work.entity.DoorwayCategory;

import cn.hutool.core.util.StrUtil;

/**
 * <p>
 * 门店分类表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-03
 */
@Service
public class DoorwayCategoryServiceImpl extends ServiceImpl<DoorwayCategoryMapper, DoorwayCategory>
		implements DoorwayCategoryService {

	/**
	 * @dec 获取门店分类列表
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public R queryList(Map<String, Object> params) {
		Integer current = MapUtils.getInteger(params, "current", 1);
		Integer size = MapUtils.getInteger(params, "size", 15);
		String name = MapUtils.getString(params, "name");
		String beginTime = MapUtils.getString(params, "beginTime");
		String endTime = MapUtils.getString(params, "endTime");
		Integer status = MapUtils.getInteger(params, "status");
		QueryWrapper<DoorwayCategory> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("create_time");

		if (StrUtil.isNotBlank(name)) {
			queryWrapper.and(i -> i.like("name", name));
		}
		if (StrUtil.isNotBlank(beginTime)) {
			queryWrapper.and(i -> i.ge("expire_time", beginTime));
		}
		if (StrUtil.isNotBlank(endTime)) {
			queryWrapper.and(i -> i.le("create_time", endTime + " 23:59:59"));
		}
		if (status != null) {
			queryWrapper.and(i -> i.eq("status", status));
		}

		IPage<DoorwayCategory> page = new Page<>(current, size);
		page = page(page, queryWrapper);
		return R.ok(page);
	}

}
