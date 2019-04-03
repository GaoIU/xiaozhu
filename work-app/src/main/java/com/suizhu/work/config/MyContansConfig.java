package com.suizhu.work.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@SpringBootConfiguration
@ConfigurationProperties(prefix = "suizhu.work")
@Data
public class MyContansConfig {

	private String appId = "X+i7kXSpxc6hZnct8MkHuY8fEzKHYL6/Mqt3l+bi27I=";

	private long tokenExpireTime = 3600 * 24 * 7;

	private String smsContentInvite = "【随助科技】您的朋友%s已经在小助上邀请您加入，并且您已经成为小助的会员，账号为：%s（用户手机）密码为：%s赶紧下载小助与朋友一起沟通项目吧，小助下载地址http://www.suizhu.net。";

	private int smsCodeLength = 4;

	private String smsCodeContent = "【随助科技】您正在修改密码，验证码为：%s。请不要告诉任何人，如非本人操作请忽略！";

	private long smsCodeExpiretime = 120;

	private String smsUrl = "http://api.zthysms.com/sendSms.do?username=%s&tkey=%s&password=%s&mobile=%s&content=%s";

	private String smsUsername = "yqpphy";

	private String smsPassword = "qJ6cJB";

	private String mapAk = "8jfvVOfkgITaLzeiY3BAKjzUbr5GloSz";

	private String mapGeocoderUrl = "http://api.map.baidu.com/geocoder/v2/?address=%s&output=json&ak=%s";

	private String mapRoutematrixUrl = "http://api.map.baidu.com/routematrix/v2/driving?output=json&origins=%s&destinations=%s&ak=%s";

	private String fdfsServer;

	private String workAvatar;

	private String groupName = "group1";

	private String fdfsPath = "fdfs:";

	private String fdfsCurr = "curr:";

}
