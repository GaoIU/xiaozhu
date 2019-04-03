package com.suizhu.work.file.service.impl;

import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.FdfsClient;
import com.suizhu.common.core.R;
import com.suizhu.common.core.RedisClient;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.common.exception.MyException;
import com.suizhu.work.config.MyContansConfig;
import com.suizhu.work.entity.FileAudio;
import com.suizhu.work.entity.FileVideo;
import com.suizhu.work.file.mapper.FileVideoMapper;
import com.suizhu.work.file.service.FileVideoService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 工程项目记录视频文件表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@Service
@AllArgsConstructor
public class FileVideoServiceImpl extends ServiceImpl<FileVideoMapper, FileVideo> implements FileVideoService {

	private final MyContansConfig myContansConfig;

	private final AppendFileStorageClient appendFileStorageClient;

	private final RedisClient redisClient;

	private final FdfsClient fdfsClient;

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
	@Override
	public R upload(String filename, String fileId, Integer chunk, Integer chunkSize, MultipartFile file,
			String fileTime, String userId) throws Exception {
		String groupName = myContansConfig.getGroupName();
		InputStream inputStream = file.getInputStream();
		long size = file.getSize();

		if (chunkSize == 0 && StrUtil.isBlank(fileId)) {// 第一次上传
			StorePath storePath = appendFileStorageClient.uploadAppenderFile(groupName, inputStream, size,
					FilenameUtils.getExtension(filename));
			if (storePath == null) {
				throw new MyException("上传失败！");
			}

			fileId = storePath.getFullPath();
			redisClient.set(myContansConfig.getFdfsPath() + fileId, storePath.getPath());
		} else {// 文件续传
			Object curr = redisClient.get(myContansConfig.getFdfsCurr() + fileId);
			if (curr == null) {
				throw new MyException("无法获取当前文件的curr！");
			}

			int currInt = (int) curr;
			if (chunk < currInt) {
				throw new MyException("当前文件块已上传！");
			} else if (chunk > currInt) {
				throw new MyException("当前文件块需要等待上传,稍后请重试！");
			}

			Object noGroupPath = redisClient.get(myContansConfig.getFdfsPath() + fileId);
			if (noGroupPath == null) {
				throw new MyException("无法获取上传远程服务器文件！");
			}

			appendFileStorageClient.modifyFile(groupName, noGroupPath.toString(), inputStream, size,
					currInt * chunkSize);
		}

		if (chunk + 1 == chunkSize) {// 已完成最后一次分片上传
			FileVideo fileVideo = new FileVideo();
			fileVideo.setFileId(fileId);
			fileVideo.setFileUrl(myContansConfig.getFdfsServer() + fileId);
			fileVideo.setStatus(FileAudio.STATUS_TEMP);
			fileVideo.setTimes(fileTime);
			fileVideo.setUserId(userId);
			save(fileVideo);

			redisClient.del(myContansConfig.getFdfsPath() + fileId, myContansConfig.getFdfsCurr() + fileId);
		}

		return R.ok(fileId);
	}

	/**
	 * @dec 上传视频文件
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param token
	 * @param file
	 * @param times
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R upload(String token, MultipartFile file, String times) {
		String userId = CommonUtil.decodeToken(token).getStr("id");
		String upload = fdfsClient.upload(file);
		String fileUrl = myContansConfig.getFdfsServer() + upload;

		FileVideo fileVideo = new FileVideo(userId, upload, fileUrl, times);
		boolean save = save(fileVideo);
		if (save) {
			return R.ok(upload);
		}

		return R.error();
	}

	/**
	 * @dec 上传视频封面
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param file
	 * @param fileId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R uploadCover(MultipartFile file, String fileId) {
		String upload = fdfsClient.upload(file);
		String fileUrl = myContansConfig.getFdfsServer() + upload;

		FileVideo fileVideo = getOne("file_id", SqlEmnus.EQ, fileId);
		fileVideo.setCoverId(upload);
		fileVideo.setCoverUrl(fileUrl);
		boolean update = updateById(fileVideo);
		if (update) {
			return R.ok(upload);
		}

		return R.error();
	}

}
