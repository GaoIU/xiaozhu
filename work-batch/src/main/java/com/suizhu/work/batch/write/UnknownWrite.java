package com.suizhu.work.batch.write;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.suizhu.work.entity.UserMessage;
import com.suizhu.work.user.service.UserMessageService;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UnknownWrite implements ItemWriter<List<UserMessage>> {

	private final UserMessageService userMessageService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void write(List<? extends List<UserMessage>> items) throws Exception {
		if (CollUtil.isNotEmpty(items)) {
			items.forEach(i -> {
				userMessageService.saveBatch(i);
			});
		}
	}

}
