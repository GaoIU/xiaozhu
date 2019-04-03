package com.suizhu.work.batch.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.suizhu.work.entity.FileAudio;
import com.suizhu.work.entity.FileImage;
import com.suizhu.work.entity.FileVideo;

import cn.hutool.core.collection.CollUtil;

/**
 * 要清除的图片文件数据处理
 * 
 * @author gaochao
 * @date Mar 6, 2019
 */
@Component
public class CleanFileProcess implements ItemProcessor<Map<String, Object>, Map<String, List<String>>> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<String>> process(Map<String, Object> item) throws Exception {
		if (CollUtil.isNotEmpty(item)) {
			Map<String, List<String>> data = new HashMap<>(6);

			List<FileAudio> audios = (List<FileAudio>) item.get("audios");
			if (CollUtil.isNotEmpty(audios)) {
				List<String> audIds = new ArrayList<>(audios.size());
				List<String> audFileIds = new ArrayList<>(audios.size());
				audios.forEach(fa -> {
					audIds.add(fa.getId());
					audFileIds.add(fa.getFileId());
				});

				data.put("audIds", audIds);
				data.put("audFileIds", audFileIds);
			}

			List<FileImage> images = (List<FileImage>) item.get("audios");
			if (CollUtil.isNotEmpty(images)) {
				List<String> imgIds = new ArrayList<>(images.size());
				List<String> imgFileIds = new ArrayList<>(images.size());
				images.forEach(fi -> {
					imgIds.add(fi.getId());
					imgFileIds.add(fi.getFileId());
				});

				data.put("imgIds", imgIds);
				data.put("imgFileIds", imgFileIds);
			}

			List<FileVideo> videos = (List<FileVideo>) item.get("videos");
			if (CollUtil.isNotEmpty(videos)) {
				List<String> vidIds = new ArrayList<>(videos.size());
				List<String> vidFileIds = new ArrayList<>(videos.size());
				videos.forEach(fv -> {
					vidIds.add(fv.getId());
					vidFileIds.add(fv.getFileId());
					vidFileIds.add(fv.getCoverId());
				});

				data.put("vidIds", vidIds);
				data.put("vidFileIds", vidFileIds);
			}

			if (CollUtil.isNotEmpty(data)) {
				return data;
			}

		}

		return null;
	}

}
