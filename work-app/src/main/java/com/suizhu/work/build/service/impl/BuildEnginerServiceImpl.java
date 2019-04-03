package com.suizhu.work.build.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.common.util.SpringContextHolder;
import com.suizhu.work.async.service.AsnycService;
import com.suizhu.work.build.mapper.BuildEnginerMapper;
import com.suizhu.work.build.service.BuildEnginerService;
import com.suizhu.work.build.service.BuildTipService;
import com.suizhu.work.doorway.service.DoorwayModelService;
import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildProjectLog;
import com.suizhu.work.entity.BuildTip;
import com.suizhu.work.entity.Doorway;
import com.suizhu.work.entity.DoorwayModel;
import com.suizhu.work.entity.LogBuildEnginer;
import com.suizhu.work.log.service.LogBuildEnginerService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 筹建工程表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@Service
@AllArgsConstructor
public class BuildEnginerServiceImpl extends ServiceImpl<BuildEnginerMapper, BuildEnginer>
		implements BuildEnginerService {

	private final AsnycService asnycService;

	private final DoorwayModelService doorwayModelService;

	private final LogBuildEnginerService logBuildEnginerService;

	private final BuildTipService buildTipService;

	/**
	 * @dec 添加筹建工程
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildEnginer
	 * @param json
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R addBuildEnginer(BuildEnginer buildEnginer, JSONObject json) {
		boolean save = save(buildEnginer);
		if (save) {
			asnycService.addBuildEnginer(buildEnginer, json);

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改筹建工程
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildEnginer
	 * @param json
	 * @param old
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R editBuildEnginer(BuildEnginer buildEnginer, JSONObject json, BuildEnginer old) {
		LocalDate prepareDate = old.getPrepareDate();
		LocalDate prepare = buildEnginer.getPrepareDate();
		if (!prepareDate.isEqual(prepare)) {
			DoorwayService doorwayService = SpringContextHolder.getBean("doorwayServiceImpl", DoorwayService.class);
			Doorway doorway = doorwayService.getOne("id", SqlEmnus.EQ, old.getDoorwayId(), "category_id");
			QueryWrapper<DoorwayModel> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("doorway_id", buildEnginer.getDoorwayId())
					.and(i -> i.eq("category_id", doorway.getCategoryId())).select("model_id");
			List<String> modelIds = doorwayModelService.list(queryWrapper).stream().map(DoorwayModel::getModelId)
					.collect(Collectors.toList());
			asnycService.editModel(buildEnginer, modelIds, json.getStr("id"));
		}

		asnycService.editBuildEnginer(old, buildEnginer, json);

		boolean update = updateById(buildEnginer);
		if (update) {
			LogBuildEnginer logBuildEnginer = new LogBuildEnginer(json.getStr("id"), buildEnginer.getId());
			logBuildEnginerService.save(logBuildEnginer);
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 关闭通知
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R close(String id, String token, Integer remind) {
		String userId = CommonUtil.decodeToken(token).getStr("id");
		QueryWrapper<BuildTip> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id", userId).and(i -> i.eq("build_enginer_id", id));
		BuildTip buildTip = buildTipService.getOne(queryWrapper);
		buildTip.setRemind(remind);
		boolean update = buildTipService.updateById(buildTip);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 发送邮件
	 * @date Mar 15, 2019
	 * @author gaochao
	 * @param email
	 * @param bpls
	 * @param title
	 * @return
	 * @throws Exception
	 */
	@Override
	public R sendEmail(String email, List<BuildProjectLog> bpls, String title) throws Exception {
		asnycService.sendEmail(email, bpls, title);
		return R.ok();
	}

}
