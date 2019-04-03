package com.suizhu.batch.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@SpringBootConfiguration
@ConfigurationProperties(prefix = "suizhu.batch")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MyContansConfig {

	private String messageName = "小助";

	private String messageContentStart = "开工提示：%s，%s将开启以下几个工序任务：%s。请您提前准备。";

	private String messageContentOvertime = "超时提示：%s有以下几个工序任务已超时：%s。请您尽快处理。";

	private String messageContentEarly = "预警提示：%s有以下几个工序任务已预警：%s。请您及时处理。";

	private String messageContentUnknown = "待处理提示：%s有以下几个工序任务需要处理：%s。请您及时处理。";

	private String jpushAppKey = "56ab7c03668791d715fc070a";

	private String jpushMasterSecret = "cdf7858c41c5d78fd42f0b54";

	private String jpushUrl = "https://api.jpush.cn/v3/push";

	private String jpushBodyStart = "小助提醒您，%s明天有%s个即将开工的工序请您提前准备。";

	private String jpushBodyOvertime = "小助提醒您：%s截止目前有%s个超时的工序请您登陆APP查看。";

	private String jpushBodyEarly = "小助提醒您：%s今天有%s个预警的工序请您登陆APP查看。";

	private String jpushBodyUnknow = "小助提醒您：%s今天有%s个还没确认进度的工序，请您登录APP进行进度确认。";

}
