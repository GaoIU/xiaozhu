package com.suizhu.work.batch.write;

import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.suizhu.common.core.FdfsClient;
import com.suizhu.work.file.service.FileAudioService;
import com.suizhu.work.file.service.FileImageService;
import com.suizhu.work.file.service.FileVideoService;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

/**
 * 要清除的图片文件数据执行
 * 
 * @author gaochao
 * @date Mar 6, 2019
 */
@Component
@AllArgsConstructor
public class CleanFileWrite implements ItemWriter<Map<String, List<String>>> {

	private final FileAudioService fileAudioService;

	private final FileImageService fileImageService;

	private final FileVideoService fileVideoService;

	private final FdfsClient fdfsClient;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void write(List<? extends Map<String, List<String>>> items) throws Exception {
		if (CollUtil.isNotEmpty(items)) {
			items.forEach(m -> {
				List<String> audIds = m.get("audIds");
				fileAudioService.removeByIds(audIds);
				List<String> audFileIds = m.get("audFileIds");
				fdfsClient.delete(audFileIds);

				List<String> imgIds = m.get("imgIds");
				fileImageService.removeByIds(imgIds);
				List<String> imgFileIds = m.get("imgFileIds");
				fdfsClient.delete(imgFileIds);

				List<String> vidIds = m.get("vidIds");
				fileVideoService.removeByIds(vidIds);
				List<String> vidFileIds = m.get("vidFileIds");
				fdfsClient.delete(vidFileIds);
			});
		}
	}

}
