package com.suizhu.work.file.service;

import org.springframework.web.multipart.MultipartFile;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.FileAudio;

/**
 * <p>
 * 工程项目记录音频文件表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
public interface FileAudioService extends IService<FileAudio> {

	/**
	 * @dec 文件分片上传
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
	 * @dec 音频文件上传
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param token
	 * @param times
	 * @param file
	 * @return
	 */
	R upload(String token, String times, MultipartFile file);

}
