package com.suizhu.work.config.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suizhu.work.entity.SysConfig;

/**
 * <p>
 * 系统配置表 Mapper 接口
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {

	/**
	 * @dec 查询数据库表名
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @return
	 */
	List<String> queryTables();

}
