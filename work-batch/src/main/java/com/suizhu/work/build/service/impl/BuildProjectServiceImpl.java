package com.suizhu.work.build.service.impl;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.build.mapper.BuildProjectMapper;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.entity.BuildProject;

/**
 * <p>
 * 筹建项目表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@Service
public class BuildProjectServiceImpl extends ServiceImpl<BuildProjectMapper, BuildProject>
		implements BuildProjectService {

}
