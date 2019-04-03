package com.suizhu.work.user.service.impl;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.entity.UserMessage;
import com.suizhu.work.user.mapper.UserMessageMapper;
import com.suizhu.work.user.service.UserMessageService;

/**
 * <p>
 * 消息推送表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-14
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

}
