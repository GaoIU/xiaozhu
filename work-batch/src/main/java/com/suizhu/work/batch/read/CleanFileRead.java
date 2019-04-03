package com.suizhu.work.batch.read;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.work.entity.FileAudio;
import com.suizhu.work.entity.FileImage;
import com.suizhu.work.entity.FileVideo;
import com.suizhu.work.file.service.FileAudioService;
import com.suizhu.work.file.service.FileImageService;
import com.suizhu.work.file.service.FileVideoService;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

/**
 * 要清除的图片文件数据读取
 * 
 * @author gaochao
 * @date Mar 6, 2019
 */
@Component
@AllArgsConstructor
public class CleanFileRead implements ItemReader<Map<String, Object>> {

	private final FileAudioService fileAudioService;

	private final FileImageService fileImageService;

	private final FileVideoService fileVideoService;

	@Override
	public Map<String, Object> read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		String now = LocalDate.now().toString() + " 00:00:00";
		Map<String, Object> data = new HashMap<>(3);

		QueryWrapper<FileAudio> audioQuery = new QueryWrapper<>();
		audioQuery.eq("status", FileAudio.STATUS_TEMP).and(i -> i.lt("create_time", now));
		audioQuery.select("id", "file_id");
		List<FileAudio> audios = fileAudioService.list(audioQuery);
		if (CollUtil.isNotEmpty(audios)) {
			data.put("audios", audios);
		}

		QueryWrapper<FileImage> imageQuery = new QueryWrapper<>();
		imageQuery.eq("status", FileImage.STATUS_TEMP).and(i -> i.lt("create_time", now));
		imageQuery.select("id", "file_id");
		List<FileImage> images = fileImageService.list(imageQuery);
		if (CollUtil.isNotEmpty(images)) {
			data.put("images", images);
		}

		QueryWrapper<FileVideo> videoQuery = new QueryWrapper<>();
		videoQuery.eq("status", FileAudio.STATUS_TEMP).and(i -> i.lt("create_time", now));
		videoQuery.select("id", "file_id");
		List<FileVideo> videos = fileVideoService.list(videoQuery);
		if (CollUtil.isNotEmpty(videos)) {
			data.put("videos", videos);
		}

		if (CollUtil.isNotEmpty(data)) {
			return data;
		}

		return null;
	}

}
