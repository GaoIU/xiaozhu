package com.suizhu.work.model.service.impl;

import org.springframework.stereotype.Service;

import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.entity.Model;
import com.suizhu.work.model.mapper.ModelMapper;
import com.suizhu.work.model.service.ModelService;

/**
 * <p>
 * 模版表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService {

}
