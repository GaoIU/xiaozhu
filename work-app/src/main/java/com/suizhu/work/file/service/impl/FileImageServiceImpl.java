package com.suizhu.work.file.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.suizhu.common.core.FdfsClient;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.config.MyContansConfig;
import com.suizhu.work.entity.FileImage;
import com.suizhu.work.file.mapper.FileImageMapper;
import com.suizhu.work.file.service.FileImageService;
import com.suizhu.work.util.CommonUtil;

import lombok.AllArgsConstructor;

/**
 * <p>
 * 工程项目记录图片文件表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-06
 */
@Service
@AllArgsConstructor
public class FileImageServiceImpl extends ServiceImpl<FileImageMapper, FileImage> implements FileImageService {

	private final FdfsClient fdfsClient;

	private final MyContansConfig myContansConfig;

	/**
	 * @dec 上传文件图片
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param doorwayId
	 * @param token
	 * @param file
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R upload(String doorwayId, String token, MultipartFile file) {

		String userId = CommonUtil.decodeToken(token).getStr("id");
		String upload = fdfsClient.upload(file);
		String fileUrl = myContansConfig.getFdfsServer() + upload;
		FileImage fileImage = new FileImage(userId, doorwayId, upload, fileUrl);
		boolean save = save(fileImage);
		if (save) {
			return R.ok(upload);
		}

		return R.error();
	}

	/**
	 * @dec 获取图片列表
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> queryList(Map<String, Object> params) {
		return baseMapper.queryList(params);
	}

}
