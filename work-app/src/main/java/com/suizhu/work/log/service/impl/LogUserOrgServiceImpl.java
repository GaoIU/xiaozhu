package com.suizhu.work.log.service.impl;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.entity.LogUserOrg;
import com.suizhu.work.log.mapper.LogUserOrgMapper;
import com.suizhu.work.log.service.LogUserOrgService;

/**
 * <p>
 * 公司人员操作日志表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-13
 */
@Service
public class LogUserOrgServiceImpl extends ServiceImpl<LogUserOrgMapper, LogUserOrg> implements LogUserOrgService {

}
