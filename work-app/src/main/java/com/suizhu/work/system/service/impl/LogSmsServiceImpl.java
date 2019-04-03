package com.suizhu.work.system.service.impl;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.entity.LogSms;
import com.suizhu.work.system.mapper.LogSmsMapper;
import com.suizhu.work.system.service.LogSmsService;

/**
 * <p>
 * 短信发送日志表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-03
 */
@Service
public class LogSmsServiceImpl extends ServiceImpl<LogSmsMapper, LogSms> implements LogSmsService {

}
