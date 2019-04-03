package com.suizhu.work.doorway.service.impl;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.doorway.mapper.DoorwayModelMapper;
import com.suizhu.work.doorway.service.DoorwayModelService;
import com.suizhu.work.entity.DoorwayModel;

/**
 * <p>
 * 门店表 - 模版表 中间关联表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
@Service
public class DoorwayModelServiceImpl extends ServiceImpl<DoorwayModelMapper, DoorwayModel>
		implements DoorwayModelService {

}
