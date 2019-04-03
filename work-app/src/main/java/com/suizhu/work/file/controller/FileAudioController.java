package com.suizhu.work.file.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.suizhu.common.core.R;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.work.file.service.FileAudioService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("fileAudio")
public class FileAudioController {

	private final FileAudioService fileAudioService;

	/**
	 * @dec 分片上传音频文件
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

		return fileAudioService.upload(filename, fileId, chunk, chunkSize, file, fileTime, jo.getStr("id"));
	}

	/**
	 * @dec 上传音频文件
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param token
	 * @param times
	 * @param file
	 * @return
	 */
	@PostMapping
	public R upload(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, String times, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException();
		}

		return fileAudioService.upload(token, times, file);
	}

}
