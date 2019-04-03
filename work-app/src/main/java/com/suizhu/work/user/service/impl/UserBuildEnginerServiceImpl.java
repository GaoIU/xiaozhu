package com.suizhu.work.user.service.impl;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.entity.UserBuildEnginer;
import com.suizhu.work.user.mapper.UserBuildEnginerMapper;
import com.suizhu.work.user.service.UserBuildEnginerService;

/**
 * <p>
 * 用户表 - 筹建工程表 中间关联表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@Service
public class UserBuildEnginerServiceImpl extends ServiceImpl<UserBuildEnginerMapper, UserBuildEnginer>
		implements UserBuildEnginerService {

}
