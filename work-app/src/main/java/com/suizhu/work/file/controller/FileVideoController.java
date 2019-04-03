package com.suizhu.work.file.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.suizhu.common.core.R;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.work.file.service.FileVideoService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("fileVideo")
public class FileVideoController {

	private final FileVideoService fileVideoService;

	/**
	 * @dec 分片上传视频文件
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param filename
	 * @param fileId
	 * @param chunk
	 * @param chunkSize
	 * @param file
	 * @param fileTime
	 * @param token
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/upload")
	public R upload(String filename, String fileId, Integer chunk, Integer chunkSize, MultipartFile file,
			String fileTime, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws Exception {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException();
		}

		JSONObject jo = CommonUtil.decodeToken(token);

		return fileVideoService.upload(filename, fileId, chunk, chunkSize, file, fileTime, jo.getStr("id"));
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
	@PostMapping
	public R upload(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, MultipartFile file, String times) {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException();
		}

		return fileVideoService.upload(token, file, times);
	}

	/**
	 * @dec 上传视频封面
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param file
	 * @param fileId
	 * @return
	 */
	@PostMapping("/cover")
	public R uploadCover(MultipartFile file, String fileId) {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException();
		}

		return fileVideoService.uploadCover(file, fileId);
	}

}
