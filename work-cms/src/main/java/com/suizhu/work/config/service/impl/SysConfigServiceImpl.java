package com.suizhu.work.config.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.cms.codegan.CodeGan;
import com.suizhu.cms.codegan.GanUtil;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.config.mapper.SysConfigMapper;
import com.suizhu.work.config.service.SysConfigService;
import com.suizhu.work.entity.SysConfig;

import cn.hutool.core.util.StrUtil;

/**
 * <p>
 * 系统配置表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	/**
	 * @dec 获取后台系统配置列表
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public R queryList(Map<String, Object> params) {
		Integer current = MapUtils.getInteger(params, "current", 1);
		Integer size = MapUtils.getInteger(params, "size", 15);
		String name = MapUtils.getString(params, "name");
		Integer status = MapUtils.getInteger(params, "status");
		String beginTime = MapUtils.getString(params, "beginTime");
		String endTime = MapUtils.getString(params, "endTime");
		QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("create_time");

		if (StrUtil.isNotBlank(name)) {
			queryWrapper.and(i -> i.like("name", name));
		}
		if (status != null) {
			queryWrapper.and(i -> i.eq("`status`", status));
		}
		if (StrUtil.isNotBlank(beginTime)) {
			queryWrapper.and(i -> i.ge("create_time", beginTime));
		}
		if (StrUtil.isNotBlank(endTime)) {
			queryWrapper.and(i -> i.le("create_time", endTime + " 23:59:59"));
		}

		IPage<SysConfig> page = new Page<>(current, size);
		page = page(page, queryWrapper);
		return R.ok(page);
	}

	/**
	 * @dec 查询数据库表名
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @return
	 */
	@Override
	public List<String> queryTables() {
		return baseMapper.queryTables();
	}

	/**
	 * @dec 生成代码
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param codeGan
	 * @return
	 */
	@Override
	public boolean codeGan(CodeGan codeGan) {
		try {
			GanUtil.codeGan(driverClassName, url, username, password, codeGan);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
