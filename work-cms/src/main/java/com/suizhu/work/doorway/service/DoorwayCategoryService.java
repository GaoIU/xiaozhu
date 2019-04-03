package com.suizhu.work.doorway.service;

import java.util.Map;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.DoorwayCategory;

/**
 * <p>
 * 门店分类表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-03
 */
public interface DoorwayCategoryService extends IService<DoorwayCategory> {

	/**
	 * @dec 获取门店分类列表
	 * @date Mar 3, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	R queryList(Map<String, Object> params);

}
