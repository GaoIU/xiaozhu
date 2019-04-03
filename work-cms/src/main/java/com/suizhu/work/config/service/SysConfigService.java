package com.suizhu.work.config.service;

import java.util.List;
import java.util.Map;

import com.suizhu.cms.codegan.CodeGan;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.SysConfig;

/**
 * <p>
 * 系统配置表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
public interface SysConfigService extends IService<SysConfig> {

	/**
	 * @dec 获取后台系统配置列表
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	R queryList(Map<String, Object> params);

	/**
	 * @dec 查询数据库表名
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @return
	 */
	List<String> queryTables();

	/**
	 * @dec 生成代码
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param codeGan
	 * @return
	 */
	boolean codeGan(CodeGan codeGan);

}
