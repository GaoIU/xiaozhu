package com.suizhu.work.file.service;

import org.springframework.web.multipart.MultipartFile;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.FileVideo;

/**
 * <p>
 * 工程项目记录视频文件表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
public interface FileVideoService extends IService<FileVideo> {

	/**
	 * @dec 分片上传视屏
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param filename
	 * @param fileId
	 * @param chunk
	 * @param chunkSize
	 * @param file
	 * @param fileTime
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	R upload(String filename, String fileId, Integer chunk, Integer chunkSize, MultipartFile file, String fileTime,
			String userId) throws Exception;

	/**
	 * @dec 上传视频文件
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param token
	 * @param file
	 * @param times
	 * @return
	 */
	R upload(String token, MultipartFile file, String times);

	/**
	 * @dec 上传视频封面
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param file
	 * @param fileId
	 * @return
	 */
	R uploadCover(MultipartFile file, String fileId);

}
