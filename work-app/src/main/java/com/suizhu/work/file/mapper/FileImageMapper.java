package com.suizhu.work.file.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suizhu.work.entity.FileImage;

/**
 * <p>
 * 工程项目记录图片文件表 Mapper 接口
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-06
 */
public interface FileImageMapper extends BaseMapper<FileImage> {

	/**
	 * @dec 获取图片列表
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryList(Map<String, Object> params);

}
