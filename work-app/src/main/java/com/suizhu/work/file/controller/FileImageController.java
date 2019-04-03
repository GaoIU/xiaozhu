package com.suizhu.work.file.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.suizhu.common.core.R;
import com.suizhu.work.file.service.FileImageService;

import lombok.AllArgsConstructor;

/**
 * <p>
 * 工程项目记录图片文件表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-06
 */
@RestController
@AllArgsConstructor
@RequestMapping("fileImage")
public class FileImageController {

	private final FileImageService fileImageService;

	/**
	 * @dec 上传文件
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param doorwayId
	 * @param token
	 * @param file
	 * @return
	 */
	@PostMapping
	public R upload(String doorwayId, @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
			MultipartFile file) {
		return fileImageService.upload(doorwayId, token, file);
	}

}
