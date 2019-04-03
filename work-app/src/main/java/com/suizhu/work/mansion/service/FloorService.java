package com.suizhu.work.mansion.service;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.Floor;

/**
 * <p>
 * 楼层表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
public interface FloorService extends IService<Floor> {

	/**
	 * @dec 删除楼层
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	R del(String id);

}
