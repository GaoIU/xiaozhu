package com.suizhu.work.user.service;

import java.util.Map;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.Organization;

/**
 * <p>
 * 公司组织表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-28
 */
public interface OrganizationService extends IService<Organization> {

	/**
	 * @dec 获取公司列表
	 * @date Feb 28, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	R queryList(Map<String, Object> params);

}
