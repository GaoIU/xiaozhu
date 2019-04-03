package com.suizhu.work.async.service;

import java.util.List;

import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildProjectLog;
import com.suizhu.work.entity.UserMobile;

import cn.hutool.json.JSONObject;

/**
 * 异步服务接口
 * 
 * @author gaochao
 * @date Mar 5, 2019
 */
public interface AsnycService {

	/**
	 * @dec 异步执行筹建工程其他添加
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param buildEnginer
	 * @param json
	 */
	void addBuildEnginer(BuildEnginer buildEnginer, JSONObject json);

	/**
	 * @dec 修改模版
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildEnginer
	 * @param modelIds
	 * @param inviteId
	 */
	void editModel(BuildEnginer buildEnginer, List<String> modelIds, String inviteId);

	/**
	 * @dec 修改筹建工程
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param old
	 * @param buildEnginer
	 * @param jsonObject
	 */
	void editBuildEnginer(BuildEnginer old, BuildEnginer buildEnginer, JSONObject jsonObject);

	/**
	 * @dec 删除文件
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param ids
	 */
	void delFile(List<String> ids);

	/**
	 * @dec 发送邮件
	 * @date Mar 15, 2019
	 * @author gaochao
	 * @param email
	 * @param bpls
	 * @param title
	 * @throws Exception
	 */
	void sendEmail(String email, List<BuildProjectLog> bpls, String title) throws Exception;

	/**
	 * @dec 新增用户通讯录
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @param list
	 * @param userId
	 */
	void saveUserMobile(List<UserMobile> list, String userId);

}
