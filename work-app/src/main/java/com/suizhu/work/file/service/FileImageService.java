package com.suizhu.work.file.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.FileImage;

/**
 * <p>
 * 工程项目记录图片文件表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-06
 */
public interface FileImageService extends IService<FileImage> {

	/**
	 * @dec 上传文件图片
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param doorwayId
	 * @param token
	 * @param file
	 * @return
	 */
	R upload(String doorwayId, String token, MultipartFile file);

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
