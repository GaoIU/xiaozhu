package com.suizhu.work.user.service.impl;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.entity.Organization;
import com.suizhu.work.user.mapper.OrganizationMapper;
import com.suizhu.work.user.service.OrganizationService;

/**
 * <p>
 * 公司组织表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-28
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization>
		implements OrganizationService {

}
