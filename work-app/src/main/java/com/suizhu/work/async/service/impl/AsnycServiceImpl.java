package com.suizhu.work.async.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.FdfsClient;
import com.suizhu.common.util.EncrypUtil;
import com.suizhu.common.util.SpringContextHolder;
import com.suizhu.work.async.service.AsnycService;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.build.service.BuildTipService;
import com.suizhu.work.config.MyContansConfig;
import com.suizhu.work.doorway.service.DoorwayModelService;
import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildProjectLog;
import com.suizhu.work.entity.BuildTip;
import com.suizhu.work.entity.Doorway;
import com.suizhu.work.entity.DoorwayModel;
import com.suizhu.work.entity.FileAudio;
import com.suizhu.work.entity.FileImage;
import com.suizhu.work.entity.FileVideo;
import com.suizhu.work.entity.LogSms;
import com.suizhu.work.entity.ModelBuild;
import com.suizhu.work.entity.User;
import com.suizhu.work.entity.UserBuildEnginer;
import com.suizhu.work.entity.UserDoorway;
import com.suizhu.work.entity.UserMobile;
import com.suizhu.work.entity.UserOrg;
import com.suizhu.work.file.service.FileAudioService;
import com.suizhu.work.file.service.FileImageService;
import com.suizhu.work.file.service.FileVideoService;
import com.suizhu.work.model.service.ModelBuildService;
import com.suizhu.work.user.service.UserBuildEnginerService;
import com.suizhu.work.user.service.UserDoorwayService;
import com.suizhu.work.user.service.UserMobileService;
import com.suizhu.work.user.service.UserOrgService;
import com.suizhu.work.user.service.UserService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;

/**
 * 异步服务接口
 * 
 * @author gaochao
 * @date Mar 5, 2019
 */
@Service
@Async
@Transactional(rollbackFor = Exception.class)
public class AsnycServiceImpl implements AsnycService {

	private final UserBuildEnginerService userBuildEnginerService;

	private final UserService userService;

	private final MyContansConfig myContansConfig;

	private final DoorwayModelService doorwayModelService;

	private final ModelBuildService modelBuildService;

	private final BuildProjectService buildProjectService;

	private final FileImageService fileImageService;

	private final FileAudioService fileAudioService;

	private final FileVideoService fileVideoService;

	private final FdfsClient fdfsClient;

	private final BuildTipService buildTipService;

	private final UserDoorwayService userDoorwayService;

	private final UserOrgService userOrgService;

	private final JavaMailSender javaMailSender;

	private final UserMobileService userMobileService;

	public AsnycServiceImpl(UserBuildEnginerService userBuildEnginerService, UserService userService,
			MyContansConfig myContansConfig, DoorwayModelService doorwayModelService,
			ModelBuildService modelBuildService, BuildProjectService buildProjectService,
			FileImageService fileImageService, FileAudioService fileAudioService, FileVideoService fileVideoService,
			FdfsClient fdfsClient, BuildTipService buildTipService, UserDoorwayService userDoorwayService,
			UserOrgService userOrgService, JavaMailSender javaMailSender, UserMobileService userMobileService) {
		super();
		this.userBuildEnginerService = userBuildEnginerService;
		this.userService = userService;
		this.myContansConfig = myContansConfig;
		this.doorwayModelService = doorwayModelService;
		this.modelBuildService = modelBuildService;
		this.buildProjectService = buildProjectService;
		this.fileImageService = fileImageService;
		this.fileAudioService = fileAudioService;
		this.fileVideoService = fileVideoService;
		this.fdfsClient = fdfsClient;
		this.buildTipService = buildTipService;
		this.userDoorwayService = userDoorwayService;
		this.userOrgService = userOrgService;
		this.javaMailSender = javaMailSender;
		this.userMobileService = userMobileService;
	}

	@Value("${spring.mail.username}")
	private String fromEmail;

	/**
	 * @dec 异步执行筹建工程其他添加
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param buildEnginer
	 * @param json
	 */
	@Override
	public void addBuildEnginer(BuildEnginer buildEnginer, JSONObject json) {
		String seriesNames[] = buildEnginer.getSeriesName().split(",");
		String seriesMobiles[] = buildEnginer.getSeriesMobile().split(",");
		for (int i = 0; i < seriesNames.length; i++) {
			User user = userService.getOne("username", SqlEmnus.EQ, seriesMobiles[i], "id");
			if (user == null) {
				user = new User();
				user.setUsername(seriesMobiles[i]);
				user.setRealName(seriesNames[i]);
				user.setAvatar(myContansConfig.getWorkAvatar());
				user.setPassword(EncrypUtil.encode(User.DEFAULT_PASSWORD));
				user.setInviteId(json.getStr("id"));
				userService.save(user);

				String content = String.format(myContansConfig.getSmsContentInvite(), json.getStr("username"),
						seriesMobiles[i], User.DEFAULT_PASSWORD);
				userService.sendSms(LogSms.TYPE_INVITE, seriesMobiles[i], content);
			}

			QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
			int j = i;
			udqw.eq("doorway_id", buildEnginer.getDoorwayId()).and(f -> f.eq("mobile", seriesMobiles[j])).select("id");
			UserDoorway userDoorway = userDoorwayService.getOne(udqw);
			if (userDoorway == null) {
				userDoorway = new UserDoorway();
				userDoorway.setName(seriesNames[i]);
				userDoorway.setMobile(seriesMobiles[i]);
				userDoorway.setDoorwayId(buildEnginer.getDoorwayId());
				userDoorway.setUserId(user.getId());
				userDoorway.setType(UserDoorway.TYPE_DOORWAY);
				userDoorwayService.save(userDoorway);
			}

			UserBuildEnginer userBuildEnginer = new UserBuildEnginer(buildEnginer.getId(), userDoorway.getId());
			userBuildEnginerService.save(userBuildEnginer);

			BuildTip buildTip = new BuildTip(buildEnginer.getId(), user.getId(), null);
			buildTipService.save(buildTip);
		}

		String projectName = buildEnginer.getProjectName();
		String projectMobile = buildEnginer.getProjectMobile();
		if (StrUtil.isNotBlank(projectMobile)) {
			String[] projectNames = projectName.split(",");
			String[] projectMobiles = projectMobile.split(",");
			for (int i = 0; i < projectNames.length; i++) {
				User user = userService.getOne("username", SqlEmnus.EQ, projectMobiles[i], "id");
				if (user == null) {
					user = new User();
					user.setUsername(projectMobiles[i]);
					user.setRealName(projectNames[i]);
					user.setAvatar(myContansConfig.getWorkAvatar());
					user.setPassword(EncrypUtil.encode(User.DEFAULT_PASSWORD));
					user.setInviteId(json.getStr("id"));
					userService.save(user);

					String content = String.format(myContansConfig.getSmsContentInvite(), json.getStr("username"),
							projectMobiles[i], User.DEFAULT_PASSWORD);
					userService.sendSms(LogSms.TYPE_INVITE, projectMobiles[i], content);
				}

				QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
				int j = i;
				udqw.eq("doorway_id", buildEnginer.getDoorwayId()).and(f -> f.eq("mobile", projectMobiles[j]))
						.select("id");
				UserDoorway userDoorway = userDoorwayService.getOne(udqw);
				if (userDoorway == null) {
					userDoorway = new UserDoorway();
					userDoorway.setName(projectNames[i]);
					userDoorway.setMobile(projectMobiles[i]);
					userDoorway.setDoorwayId(buildEnginer.getDoorwayId());
					userDoorway.setUserId(user.getId());
					userDoorway.setType(UserDoorway.TYPE_DOORWAY);
					userDoorwayService.save(userDoorway);
				}

				BuildTip buildTip = new BuildTip(buildEnginer.getId(), user.getId(), null);
				buildTipService.save(buildTip);
			}
		}

		DoorwayService doorwayService = SpringContextHolder.getBean("doorwayServiceImpl", DoorwayService.class);
		Doorway doorway = doorwayService.getOne("id", SqlEmnus.EQ, buildEnginer.getDoorwayId(), "id", "org_id",
				"category_id");

		QueryWrapper<UserOrg> wrapper = new QueryWrapper<>();
		wrapper.eq("org_id", doorway.getOrgId()).and(i -> i.ne("user_id", json.getStr("id")));
		wrapper.select("id", "user_id");
		userOrgService.list(wrapper).forEach(uo -> {
			BuildTip buildTip = new BuildTip(buildEnginer.getId(), uo.getUserId(), doorway.getOrgId());
			buildTipService.save(buildTip);
		});

		LocalDate prepareDate = buildEnginer.getPrepareDate();
		QueryWrapper<DoorwayModel> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("doorway_id", buildEnginer.getDoorwayId())
				.and(i -> i.eq("category_id", doorway.getCategoryId())).select("model_id");
		List<String> modelIds = doorwayModelService.list(queryWrapper).stream().map(DoorwayModel::getModelId)
				.collect(Collectors.toList());
		List<ModelBuild> list = modelBuildService.list("model_id", SqlEmnus.IN, modelIds);

		List<BuildProject> bps = list.stream().map(mb -> {
			BuildProject buildProject = new BuildProject();
			buildProject.setName(mb.getName());
			buildProject.setSort(mb.getSort());

			LocalDate beginDate = prepareDate.plusDays(mb.getBeginDay() - 1);
			buildProject.setBeginDate(beginDate);
			Integer days = mb.getDays();
			buildProject.setEndDate(beginDate.plusDays(days - 1));
			buildProject.setPlanDays(days);

			LocalDate now = LocalDate.now();
			if (beginDate.isBefore(now) || beginDate.isEqual(now)) {
				buildProject.setStatus(BuildProject.STATUS_DOING);

				long abs = Math.abs(ChronoUnit.DAYS.between(beginDate, now));
				if (abs >= 1) {
					buildProject.setEarly(BuildProject.EARLY_WARNING);
				}
			}
			if (buildProject.getEndDate().isBefore(now)) {
				buildProject.setEarly(BuildProject.EARLY_OVERTIME_DOING);
			}
			buildProject.setType(BuildProject.TYPE_IN);
			buildProject.setBuildEnginerId(buildEnginer.getId());
			buildProject.setCreateId(json.getStr("id"));

			return buildProject;
		}).collect(Collectors.toList());

		buildProjectService.saveBatch(bps);
	}

	/**
	 * @dec 修改模版
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildEnginer
	 * @param modelIds
	 * @param inviteId
	 */
	@Override
	public void editModel(BuildEnginer buildEnginer, List<String> modelIds, String inviteId) {
		buildProjectService.remove("build_enginer_id", SqlEmnus.EQ, buildEnginer.getId());
		LocalDate prepareDate = buildEnginer.getPrepareDate();
		if (prepareDate != null) {
			List<ModelBuild> list = modelBuildService.list("model_id", SqlEmnus.IN, modelIds);

			List<BuildProject> bps = list.stream().map(mb -> {
				BuildProject buildProject = new BuildProject();
				buildProject.setName(mb.getName());
				buildProject.setSort(mb.getSort());

				LocalDate beginDate = prepareDate.plusDays(mb.getBeginDay() - 1);
				buildProject.setBeginDate(beginDate);
				Integer days = mb.getDays();
				buildProject.setEndDate(beginDate.plusDays(days - 1));
				buildProject.setPlanDays(days);

				LocalDate now = LocalDate.now();
				if (beginDate.isBefore(now) || beginDate.isEqual(now)) {
					buildProject.setStatus(BuildProject.STATUS_DOING);

					long abs = Math.abs(ChronoUnit.DAYS.between(beginDate, now));
					if (abs >= 1) {
						buildProject.setEarly(BuildProject.EARLY_WARNING);
					}
				}
				if (buildProject.getEndDate().isBefore(now)) {
					buildProject.setEarly(BuildProject.EARLY_OVERTIME_DOING);
				}
				buildProject.setType(BuildProject.TYPE_IN);
				buildProject.setBuildEnginerId(buildEnginer.getId());
				buildProject.setCreateId(inviteId);

				return buildProject;
			}).collect(Collectors.toList());

			buildProjectService.saveBatch(bps);
		}
	}

	/**
	 * @dec 修改筹建工程
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param old
	 * @param buildEnginer
	 * @param json
	 */
	@Override
	public void editBuildEnginer(BuildEnginer old, BuildEnginer buildEnginer, JSONObject json) {
		String[] seriesNames = buildEnginer.getSeriesName().split(",");
		String[] seriesMobiles = buildEnginer.getSeriesMobile().split(",");
		String delSeriesMobile = buildEnginer.getDelSeriesMobile();

		Set<String> addIds = new HashSet<>();
		for (int i = 0; i < seriesMobiles.length; i++) {
			User user = userService.getOne("username", SqlEmnus.EQ, seriesMobiles[i], "id");
			if (user == null) {
				user = new User();
				user.setUsername(seriesMobiles[i]);
				user.setRealName(seriesNames[i]);
				user.setAvatar(myContansConfig.getWorkAvatar());
				user.setPassword(EncrypUtil.encode(User.DEFAULT_PASSWORD));
				user.setInviteId(json.getStr("id"));
				userService.save(user);

				String content = String.format(myContansConfig.getSmsContentInvite(), json.getStr("username"),
						seriesMobiles[i], User.DEFAULT_PASSWORD);
				userService.sendSms(LogSms.TYPE_INVITE, seriesMobiles[i], content);
			}

			QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
			int j = i;
			udqw.eq("doorway_id", old.getDoorwayId()).and(f -> f.eq("mobile", seriesMobiles[j]));
			udqw.select("id", "name", "mobile", "type", "work_out");
			UserDoorway userDoorway = userDoorwayService.getOne(udqw);
			if (userDoorway == null) {
				userDoorway = new UserDoorway();
				userDoorway.setName(seriesNames[i]);
				userDoorway.setMobile(seriesMobiles[i]);
				userDoorway.setDoorwayId(buildEnginer.getDoorwayId());
				userDoorway.setUserId(user.getId());
				userDoorway.setType(UserDoorway.TYPE_DOORWAY);
				userDoorwayService.save(userDoorway);

				BuildTip buildTip = new BuildTip(buildEnginer.getId(), user.getId(), null);
				buildTipService.save(buildTip);

				addIds.add(userDoorway.getId());
			} else {
				userDoorway.setName(seriesNames[i]);
				userDoorway.setMobile(seriesMobiles[i]);
				if (userDoorway.getType() != UserDoorway.TYPE_DOORWAY) {
					userDoorway.setType(UserDoorway.TYPE_DOORWAY);
					addIds.add(userDoorway.getId());
				}
				userDoorwayService.updateById(userDoorway);
			}
		}

		if (CollUtil.isNotEmpty(addIds)) {
			addIds.forEach(s -> {
				UserBuildEnginer userBuildEnginer = new UserBuildEnginer(old.getId(), s);
				userBuildEnginerService.save(userBuildEnginer);
			});
		}

		String projectName = buildEnginer.getProjectName();
		String projectMobile = buildEnginer.getProjectMobile();
		String delProjectMobile = buildEnginer.getDelProjectMobile();
		if (StrUtil.isNotBlank(projectMobile)) {
			String[] projectNames = projectName.split(",");
			String[] projectMobiles = projectMobile.split(",");
			for (int i = 0; i < projectNames.length; i++) {
				User user = userService.getOne("username", SqlEmnus.EQ, projectMobiles[i], "id");
				if (user == null) {
					user = new User();
					user.setUsername(projectMobiles[i]);
					user.setRealName(projectNames[i]);
					user.setAvatar(myContansConfig.getWorkAvatar());
					user.setPassword(EncrypUtil.encode(User.DEFAULT_PASSWORD));
					user.setInviteId(json.getStr("id"));
					userService.save(user);

					String content = String.format(myContansConfig.getSmsContentInvite(), json.getStr("username"),
							projectMobiles[i], User.DEFAULT_PASSWORD);
					userService.sendSms(LogSms.TYPE_INVITE, projectMobiles[i], content);
				}

				QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
				int j = i;
				udqw.eq("doorway_id", old.getDoorwayId()).and(f -> f.eq("mobile", projectMobiles[j]));
				udqw.select("id", "name", "mobile", "type", "work_out");
				UserDoorway userDoorway = userDoorwayService.getOne(udqw);
				if (userDoorway == null) {
					userDoorway = new UserDoorway();
					userDoorway.setName(projectNames[i]);
					userDoorway.setMobile(projectMobiles[i]);
					userDoorway.setDoorwayId(old.getDoorwayId());
					userDoorway.setUserId(user.getId());
					userDoorway.setType(UserDoorway.TYPE_DOORWAY);
					userDoorwayService.save(userDoorway);

					BuildTip buildTip = new BuildTip(old.getId(), user.getId(), null);
					buildTipService.save(buildTip);
				} else {
					userDoorway.setName(projectNames[i]);
					userDoorway.setMobile(projectMobiles[i]);
					if (userDoorway.getType() != UserDoorway.TYPE_DOORWAY) {
						userDoorway.setType(UserDoorway.TYPE_DOORWAY);
						addIds.add(userDoorway.getId());
					}
					userDoorwayService.updateById(userDoorway);
				}
			}
		}

		if (StrUtil.isNotBlank(delSeriesMobile)) {
			List<String> delSeriesMobiles = new ArrayList<>(Arrays.asList(delSeriesMobile.split(",")));
			List<String> userIds = userService.list("username", SqlEmnus.IN, delSeriesMobiles, "id").stream()
					.map(User::getId).collect(Collectors.toList());

			QueryWrapper<BuildTip> btqw = new QueryWrapper<>();
			btqw.eq("build_enginer_id", old.getId()).and(i -> i.in("user_id", userIds));
			buildTipService.remove(btqw);

			QueryWrapper<UserDoorway> wrapper = new QueryWrapper<>();
			wrapper.in("mobile", delSeriesMobiles).and(i -> i.eq("doorway_id", old.getDoorwayId())).select("id");
			List<String> udIds = userDoorwayService.list(wrapper).stream().map(UserDoorway::getId)
					.collect(Collectors.toList());
			if (CollUtil.isNotEmpty(udIds)) {
				userDoorwayService.removeByIds(udIds);
			}

			QueryWrapper<UserBuildEnginer> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("build_enginer_id", old.getId()).and(i -> i.in("user_doorway_id", udIds));
			userBuildEnginerService.remove(queryWrapper);
		}

		if (StrUtil.isNotBlank(delProjectMobile)) {
			List<String> delProjectMobiles = new ArrayList<>(Arrays.asList(delProjectMobile.split(",")));
			List<String> userIds = userService.list("username", SqlEmnus.IN, delProjectMobiles, "id").stream()
					.map(User::getId).collect(Collectors.toList());

			QueryWrapper<BuildTip> btqw = new QueryWrapper<>();
			btqw.eq("build_enginer_id", old.getId()).and(i -> i.in("user_id", userIds));
			buildTipService.remove(btqw);

			QueryWrapper<UserDoorway> wrapper = new QueryWrapper<>();
			wrapper.in("mobile", delProjectMobiles).and(i -> i.eq("doorway_id", old.getDoorwayId())).select("id");
			List<String> udIds = userDoorwayService.list(wrapper).stream().map(UserDoorway::getId)
					.collect(Collectors.toList());
			if (CollUtil.isNotEmpty(udIds)) {
				userDoorwayService.removeByIds(udIds);
			}

			QueryWrapper<UserBuildEnginer> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("build_enginer_id", old.getId()).and(i -> i.in("user_doorway_id", udIds));
			userBuildEnginerService.remove(queryWrapper);
		}
	}

	/**
	 * @dec 删除文件
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param ids
	 */
	@Override
	public void delFile(List<String> ids) {
		List<FileImage> images = fileImageService.list("build_project_log_id", SqlEmnus.IN, ids, "file_id");
		List<String> fileIds = new ArrayList<>();
		if (CollUtil.isNotEmpty(images)) {
			fileImageService.remove("build_project_log_id", SqlEmnus.IN, ids);
			List<String> imgs = images.stream().map(FileImage::getFileId).collect(Collectors.toList());
			fileIds.addAll(imgs);
		}

		List<FileAudio> audios = fileAudioService.list("build_project_log_id", SqlEmnus.IN, ids, "file_id");
		if (CollUtil.isNotEmpty(audios)) {
			fileAudioService.remove("build_project_log_id", SqlEmnus.IN, ids);
			List<String> auds = audios.stream().map(FileAudio::getFileId).collect(Collectors.toList());
			fileIds.addAll(auds);
		}

		List<FileVideo> videos = fileVideoService.list("build_project_log_id", SqlEmnus.IN, ids, "file_id");
		if (CollUtil.isNotEmpty(videos)) {
			fileVideoService.remove("build_project_log_id", SqlEmnus.IN, ids);
			List<String> vies = videos.stream().map(FileVideo::getFileId).collect(Collectors.toList());
			fileIds.addAll(vies);
		}

		if (CollUtil.isNotEmpty(fileIds)) {
			fdfsClient.delete(fileIds);
		}
	}

	/**
	 * @dec 发送邮件
	 * @date Mar 15, 2019
	 * @author gaochao
	 * @param email
	 * @param bpls
	 * @param title
	 * @throws Exception
	 */
	@Override
	public void sendEmail(String email, List<BuildProjectLog> bpls, String title) throws Exception {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
		mimeMessageHelper.setFrom(fromEmail);
		mimeMessageHelper.setTo(email);
		mimeMessageHelper.setSubject(title);

		StringBuffer sb = new StringBuffer();
		sb.append(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
		sb.append("<title>" + title + "</title>");
		sb.append("</head>");
		sb.append("<body style=\"margin: 0 auto; padding: 0; text-align: center;\">");
		sb.append(
				"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"margin: 0 auto; padding: 0; width:100% !important; line-height: 100% !important;\">");
		sb.append("<tr style=\"height: 200px;\">");
		sb.append("<td colspan=\"2\" align=\"center\">");
		sb.append("<span style=\"font-size: 36px; font-weight: 600; display: block;\">" + title + "</span>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr style=\"height: 100px\">");
		sb.append("<td align=\"center\"><b>时间</b></td>");
		sb.append("<td align=\"center\"><b>内容</b></td>");
		sb.append("<td align=\"center\"><b>所属工序</b></td>");
		sb.append("</tr>");

		bpls.forEach(bpl -> {
			String remark = bpl.getRemark();
			if (StrUtil.isNotBlank(remark)) {
				sb.append("</tr>");
				String date = bpl.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				sb.append("<td align=\"center\"><b>" + date + "</b></td>");
				sb.append("<td align=\"center\"><b>" + remark + "</b></td>");
				BuildProject buildProject = buildProjectService.getOne("id", SqlEmnus.EQ, bpl.getBuildProjectId(),
						"name");
				sb.append("<td align=\"center\"><b>" + buildProject.getName() + "</b></td>");
				sb.append("</tr>");
			}
		});

		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");

		mimeMessageHelper.setText(sb.toString(), true);

		javaMailSender.send(mimeMessage);
	}

	/**
	 * @dec 新增用户通讯录
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @param list
	 * @param userId
	 */
	@Override
	public void saveUserMobile(List<UserMobile> list, String userId) {
		List<UserMobile> saveBatch = new ArrayList<>(list.size());
		list.forEach(um -> {
			QueryWrapper<UserMobile> wrapper = new QueryWrapper<>();
			wrapper.eq("mobile", um.getMobile()).and(i -> i.eq("user_id", userId));
			int count = userMobileService.count(wrapper);
			if (count <= 0) {
				um.setUserId(userId);
				String name = CommonUtil.filterEmoji(um.getName());
				um.setName(name);
				saveBatch.add(um);
			}
		});

		userMobileService.saveBatch(saveBatch);
	}

}
