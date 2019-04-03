package com.suizhu.cms.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suizhu.cms.entity.SysResource;

/**
 * <p>
 * 后台资源表 Mapper 接口
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
public interface SysResourceMapper extends BaseMapper<SysResource> {

	/**
	 * @dec 根据ID查询信息
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	Map<String, Object> view(String id);

}
