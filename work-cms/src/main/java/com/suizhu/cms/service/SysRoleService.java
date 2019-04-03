package com.suizhu.cms.service;

import java.util.List;
import java.util.Map;

import com.suizhu.cms.entity.SysRole;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;

/**
 * <p>
 * 后台角色表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
public interface SysRoleService extends IService<SysRole> {

	/**
	 * @dec 根据后台用户ID查询
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysUserId
	 * @return
	 */
	List<SysRole> findBySysUserId(String sysUserId);

	/**
	 * @dec 获取后台角色列表
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	R queryList(Map<String, Object> params);

	/**
	 * @dec 新增后台角色
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysRole
	 * @return
	 */
	R add(SysRole sysRole);

	/**
	 * @dec 修改后台角色
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysRole
	 * @return
	 */
	R edit(SysRole sysRole);

	/**
	 * @dec 删除后台角色
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param idList
	 * @return
	 */
	R del(List<String> idList);

}
