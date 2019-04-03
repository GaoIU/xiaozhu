package com.suizhu.cms.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.cms.entity.SysResource;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;

/**
 * <p>
 * 后台资源表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
public interface SysResourceService extends IService<SysResource> {

	/**
	 * @dec 根据后台用户ID和动态条件查询
	 * @date Feb 20, 2019
	 * @author gaochao
	 * @param sysUserId
	 * @param wrapper
	 * @return
	 */
	List<SysResource> findByWrapper(String sysUserId, QueryWrapper<SysResource> wrapper);

	/**
	 * @dec 获取资源树
	 * @date Feb 20, 2019
	 * @author gaochao
	 * @param list
	 * @param childName
	 * @param conditionType
	 * @return
	 */
	List<Map<String, Object>> getMenu(List<SysResource> list, String childName, Integer conditionType);

	/**
	 * @dec 获取后台资源列表
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	R queryList(Map<String, Object> params);

	/**
	 * @dec 根据ID查询信息
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	Map<String, Object> view(String id);

	/**
	 * @dec 根据后台角色ID获取ID结果集
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param sysRoleId
	 * @return
	 */
	List<String> getIdsBySysRoleId(String sysRoleId);

	/**
	 * @dec 获取角色权限树菜单
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param list
	 * @param ids
	 * @return
	 */
	List<Map<String, Object>> getPermission(List<SysResource> list, List<String> ids);

}
