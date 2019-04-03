package com.suizhu.work.user.service.impl;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.entity.User;
import com.suizhu.work.user.mapper.UserMapper;
import com.suizhu.work.user.service.UserService;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-28
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
