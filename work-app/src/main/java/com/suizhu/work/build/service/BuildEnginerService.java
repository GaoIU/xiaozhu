package com.suizhu.work.build.service;

import java.util.List;

import com.suizhu.common.core.R;
import com.suizhu.common.core.service.IService;
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildProjectLog;

import cn.hutool.json.JSONObject;

/**
 * <p>
 * 筹建工程表 服务类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
public interface BuildEnginerService extends IService<BuildEnginer> {

	/**
	 * @dec 添加筹建工程
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param buildEnginer
	 * @param json
	 * @return
	 */
	R addBuildEnginer(BuildEnginer buildEnginer, JSONObject json);

	/**
	 * @dec 修改筹建工程
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildEnginer
	 * @param json
	 * @param old
	 * @return
	 */
	R editBuildEnginer(BuildEnginer buildEnginer, JSONObject json, BuildEnginer old);

	/**
	 * @dec 关闭通知
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @param remind
	 * @return
	 */
	R close(String id, String token, Integer remind);

	/**
	 * @dec 发送邮件
	 * @date Mar 15, 2019
	 * @author gaochao
	 * @param email
	 * @param bpls
	 * @param doorwayName
	 * @return
	 * @throws Exception
	 */
	R sendEmail(String email, List<BuildProjectLog> bpls, String doorwayName) throws Exception;

}
