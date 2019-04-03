package com.suizhu.work.build.service.impl;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.build.mapper.BuildProjectLogMapper;
import com.suizhu.work.build.service.BuildProjectLogService;
import com.suizhu.work.entity.BuildProjectLog;

/**
 * <p>
 * 工程项目记录表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@Service
public class BuildProjectLogServiceImpl extends ServiceImpl<BuildProjectLogMapper, BuildProjectLog>
		implements BuildProjectLogService {

}
