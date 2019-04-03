package com.suizhu.work.batch.process;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.suizhu.batch.config.MyContansConfig;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.work.build.service.BuildEnginerService;
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildTip;
import com.suizhu.work.entity.Doorway;
import com.suizhu.work.entity.User;
import com.suizhu.work.entity.UserMessage;
import com.suizhu.work.jpush.JpushUtil;
import com.suizhu.work.user.service.UserService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UnknownProcess implements ItemProcessor<Map<String, Object>, List<UserMessage>> {

	private final BuildEnginerService buildEnginerService;

	private final MyContansConfig myContansConfig;

	private final UserService userService;

	@SuppressWarnings("unchecked")
	@Override
	public List<UserMessage> process(Map<String, Object> item) throws Exception {
		if (CollUtil.isNotEmpty(item)) {
			List<Doorway> doorways = (List<Doorway>) item.get("doorways");
			List<BuildProject> list = (List<BuildProject>) item.get("list");
			List<BuildTip> bts = (List<BuildTip>) item.get("bts");
			String name = myContansConfig.getMessageName();
			String jpushAppKey = myContansConfig.getJpushAppKey();
			String jpushMasterSecret = myContansConfig.getJpushMasterSecret();
			String jpushUrl = myContansConfig.getJpushUrl();
			String jpushBodyUnknow = myContansConfig.getJpushBodyUnknow();
			String messageContentUnknown = myContansConfig.getMessageContentUnknown();
			List<UserMessage> result = new ArrayList<>();

			doorways.forEach(d -> {
				BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, d.getId(), "id");
				int count = 0;
				StringBuffer sb = new StringBuffer("");
				for (BuildProject bp : list) {
					if (StrUtil.equals(buildEnginer.getId(), bp.getBuildEnginerId())) {
						if (count > 0) {
							sb.append("、");
						}
						sb.append(bp.getName());
						count++;
					}
				}

				if (count > 0) {
					Set<String> userIds = new HashSet<>();
					bts.forEach(bt -> {
						if (StrUtil.equals(buildEnginer.getId(), bt.getBuildEnginerId())) {
							userIds.add(bt.getUserId());
						}
					});

					if (CollUtil.isNotEmpty(userIds)) {
						Set<String> usernames = userService.list("id", SqlEmnus.IN, userIds, "username").stream()
								.map(User::getUsername).collect(Collectors.toSet());

						String message = String.format(jpushBodyUnknow, d.getName(), count);
						JpushUtil.jpush(jpushAppKey, jpushMasterSecret, jpushUrl, usernames, message);

						String body = String.format(messageContentUnknown, d.getName(), sb.toString());
						userIds.forEach(s -> {
							UserMessage userMessage = new UserMessage(name, body, UserMessage.TYPE_NOT_FLUSH, d.getId(),
									s);
							result.add(userMessage);
						});
					}
				}
			});

			if (CollUtil.isNotEmpty(result)) {
				return result;
			}
		}

		return null;
	}

}
