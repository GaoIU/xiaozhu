package com.suizhu.work.build.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.common.util.EncrypUtil;
import com.suizhu.common.util.SpringContextHolder;
import com.suizhu.work.async.service.AsnycService;
import com.suizhu.work.build.mapper.BuildProjectMapper;
import com.suizhu.work.build.service.BuildEnginerService;
import com.suizhu.work.build.service.BuildProjectLogService;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.build.service.BuildTipService;
import com.suizhu.work.config.MyContansConfig;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildProjectLog;
import com.suizhu.work.entity.BuildTip;
import com.suizhu.work.entity.FileAudio;
import com.suizhu.work.entity.FileImage;
import com.suizhu.work.entity.FileVideo;
import com.suizhu.work.entity.Floor;
import com.suizhu.work.entity.LogSms;
import com.suizhu.work.entity.Mansion;
import com.suizhu.work.entity.Room;
import com.suizhu.work.entity.User;
import com.suizhu.work.entity.UserDoorway;
import com.suizhu.work.file.service.FileAudioService;
import com.suizhu.work.file.service.FileImageService;
import com.suizhu.work.file.service.FileVideoService;
import com.suizhu.work.mansion.service.FloorService;
import com.suizhu.work.mansion.service.MansionService;
import com.suizhu.work.mansion.service.RoomService;
import com.suizhu.work.user.service.UserDoorwayService;
import com.suizhu.work.user.service.UserService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 筹建项目表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@Service
@AllArgsConstructor
public class BuildProjectServiceImpl extends ServiceImpl<BuildProjectMapper, BuildProject>
		implements BuildProjectService {

	private final UserService userService;

	private final UserDoorwayService userDoorwayService;

	private final MyContansConfig myContansConfig;

	private final BuildProjectLogService buildProjectLogService;

	private final RoomService roomService;

	private final FloorService floorService;

	private final MansionService mansionService;

	private final FileImageService fileImageService;

	private final FileAudioService fileAudioService;

	private final FileVideoService fileVideoService;

	private final BuildTipService buildTipService;

	/**
	 * @dec 获取项目列表
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param buildEnginerId
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> queryList(Map<String, Object> params) {
		return baseMapper.queryList(params);
	}

	/**
	 * @dec 获取下一个项目列表
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> nextQuery(Map<String, Object> params) {
		return baseMapper.nextQuery(params);
	}

	/**
	 * @dec 获取条数
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public int queryCount(Map<String, Object> params) {
		return baseMapper.queryCount(params);
	}

	/**
	 * @dec 新增工序
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildProject
	 * @param decodeToken
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R add(BuildProject buildProject, JSONObject json) {
		String seriesName = buildProject.getSeriesName();
		String seriesMobile = buildProject.getSeriesMobile();

		if (StrUtil.isNotBlank(seriesMobile)) {
			User user = userService.getOne("username", SqlEmnus.EQ, seriesMobile, "id");
			if (user == null) {
				user = new User();
				user.setUsername(seriesMobile);
				user.setRealName(seriesName);
				user.setAvatar(myContansConfig.getWorkAvatar());
				user.setPassword(EncrypUtil.encode(User.DEFAULT_PASSWORD));
				user.setInviteId(json.getStr("id"));
				userService.save(user);

				BuildTip buildTip = new BuildTip(buildProject.getBuildEnginerId(), user.getId(), null);
				buildTipService.save(buildTip);

				String content = String.format(myContansConfig.getSmsContentInvite(), json.getStr("username"),
						seriesMobile, User.DEFAULT_PASSWORD);
				userService.sendSms(LogSms.TYPE_INVITE, seriesMobile, content);
			}

			QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
			BuildEnginerService buildEnginerService = SpringContextHolder.getBean("buildEnginerServiceImpl",
					BuildEnginerService.class);
			String doorwayId = buildEnginerService
					.getOne("id", SqlEmnus.EQ, buildProject.getBuildEnginerId(), "doorway_id").getDoorwayId();
			udqw.eq("doorway_id", doorwayId).and(f -> f.eq("mobile", seriesMobile)).select("id", "type");
			UserDoorway userDoorway = userDoorwayService.getOne(udqw);
			if (userDoorway == null) {
				userDoorway = new UserDoorway();
				userDoorway.setName(seriesName);
				userDoorway.setMobile(seriesMobile);
				userDoorway.setDoorwayId(doorwayId);
				userDoorway.setUserId(user.getId());
				userDoorway.setType(UserDoorway.TYPE_PROJECT);
				userDoorwayService.save(userDoorway);
			} else {
				if (UserDoorway.TYPE_DOORWAY != userDoorway.getType()) {
					userDoorway.setType(UserDoorway.TYPE_PROJECT);
					userDoorwayService.updateById(userDoorway);
				}
			}

			buildProject.setSeriesId(userDoorway.getId());
		}

		boolean save = save(buildProject);
		if (save) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 删除工序
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param buildProject
	 * @param json
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R del(BuildProject buildProject, JSONObject json) {
		QueryWrapper<BuildProject> wrapper = new QueryWrapper<>();
		wrapper.eq("build_enginer_id", buildProject.getBuildEnginerId());
		wrapper.and(i -> i.eq("series_id", buildProject.getSeriesId()));
		int count = count(wrapper);
		if (count > 0) {
			UserDoorway userDoorway = userDoorwayService.getById(buildProject.getSeriesId());
			userDoorway.setType(UserDoorway.TYPE_NORMAL);
			userDoorwayService.updateById(userDoorway);
		}

		List<BuildProjectLog> list = buildProjectLogService.list("build_project_id", SqlEmnus.EQ, buildProject.getId(),
				"id");
		if (CollUtil.isNotEmpty(list)) {
			List<String> ids = list.stream().map(BuildProjectLog::getId).collect(Collectors.toList());
			buildProjectLogService.removeByIds(ids);

			AsnycService asnycService = SpringContextHolder.getBean("asnycServiceImpl", AsnycService.class);
			asnycService.delFile(ids);
		}

		boolean remove = removeById(buildProject.getId());
		if (remove) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * @dec 获取项目日志
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param userId
	 * @return
	 */
	@Override
	public List<Map<String, Object>> info(String id, String userId) {
		QueryWrapper<BuildProjectLog> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("build_project_id", id).orderByDesc("create_time");
		List<BuildProjectLog> list = buildProjectLogService.list(queryWrapper);
		if (CollUtil.isEmpty(list)) {
			return new ArrayList<>(0);
		}

		LocalDate now = LocalDate.now();
		List<Map<String, Object>> collect = list.stream().map(bpl -> {
			Map<String, Object> data = new HashMap<>(12);
			data.put("id", bpl.getId());
			User user = userService.getOne("id", SqlEmnus.EQ, bpl.getUserId(), "avatar", "real_name");
			data.put("realName", user.getRealName());
			data.put("avatar", user.getAvatar());

			LocalDateTime createTime = bpl.getCreateTime();
			data.put("isEdit", false);
			if (StrUtil.equals(userId, bpl.getUserId()) && now.isEqual(createTime.toLocalDate())) {
				data.replace("isEdit", true);
			}

			data.put("remark", bpl.getRemark());
			Integer overdueDays = bpl.getOverdueDays() != null ? bpl.getOverdueDays() : 0;
			data.put("overdueDays", overdueDays);
			data.put("early", bpl.getEarly());
			if (StrUtil.isNotBlank(bpl.getRoomId())) {
				Room room = roomService.getById(bpl.getRoomId());
				if (room != null) {
					Floor floor = floorService.getById(room.getFloorId());
					Mansion mansion = mansionService.getById(floor.getMansionId());
					data.put("address", mansion.getName() + floor.getName() + room.getName());
				}
			} else {
				data.put("address", "");
			}

			List<Map<String, Object>> files = new ArrayList<>();
			files.addAll(getVideo(bpl.getId()));
			List<String> images = getImages(bpl.getId());
			images.forEach(s -> {
				Map<String, Object> im = new HashMap<>(1);
				im.put("fileUrl", s);
				files.add(im);
			});
			data.put("audio", getAudio(bpl.getId()));
			data.put("files", files);

			if (now.getYear() == createTime.getYear()) {
				data.put("time", createTime.format(DateTimeFormatter.ofPattern("MM.dd HH:mm")));
			} else {
				data.put("time", createTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
			}

			return data;
		}).collect(Collectors.toList());

		return collect;
	}

	/**
	 * @dec 获取图片文件
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param bplId
	 * @return
	 */
	private List<String> getImages(String bplId) {
		QueryWrapper<FileImage> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("build_project_log_id", bplId);
		queryWrapper.and(i -> i.eq("status", FileImage.STATUS_PERM)).and(i -> i.eq("type", FileImage.TYPE_PROJECT));
		queryWrapper.select("file_url");
		List<FileImage> list = fileImageService.list(queryWrapper);
		if (CollUtil.isEmpty(list)) {
			return new ArrayList<>(0);
		}

		return list.stream().map(FileImage::getFileUrl).collect(Collectors.toList());
	}

	/**
	 * @dec 获取音频文件
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param bplId
	 * @return
	 */
	private Map<String, Object> getAudio(String bplId) {
		QueryWrapper<FileAudio> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("build_project_log_id", bplId);
		queryWrapper.and(i -> i.eq("status", FileAudio.STATUS_PERM));
		queryWrapper.select("file_url", "times");
		FileAudio audio = fileAudioService.getOne(queryWrapper);
		Map<String, Object> data = new HashMap<>(2);
		if (audio != null) {
			data.put("fileUrl", audio.getFileUrl());
			data.put("times", audio.getTimes());
		}
		return data;
	}

	/**
	 * @dec 获取视频文件
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param bplId
	 * @return
	 */
	private List<Map<String, Object>> getVideo(String bplId) {
		QueryWrapper<FileVideo> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("build_project_log_id", bplId);
		queryWrapper.and(i -> i.eq("status", FileVideo.STATUS_PERM));
		queryWrapper.select("file_url", "times", "cover_url");
		List<FileVideo> list = fileVideoService.list(queryWrapper);
		if (CollUtil.isEmpty(list)) {
			return new ArrayList<>(0);
		}

		return list.stream().map(fv -> {
			Map<String, Object> data = new HashMap<>(3);
			data.put("fileUrl", fv.getFileUrl());
			data.put("times", fv.getTimes());
			data.put("coverUrl", fv.getCoverUrl());
			return data;
		}).collect(Collectors.toList());
	}

	/**
	 * @dec 分配组长
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param userId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R setSernice(String id, String userDoorwayId) {
		BuildProject buildProject = getById(id);
		String seriesId = buildProject.getSeriesId();
		if (StrUtil.isNotBlank(seriesId)) {
			String[] seriesIds = seriesId.split(",");
			String[] userDoorwayIds = userDoorwayId.split(",");
			boolean anyMatch = Stream.of(seriesIds)
					.anyMatch(serId -> Stream.of(userDoorwayIds).anyMatch(udId -> StrUtil.equals(serId, udId)));
			if (anyMatch) {
				return R.error("不能重复分配！");
			}

			seriesId = seriesId + "," + userDoorwayId;
		} else {
			seriesId = userDoorwayId;
		}
		buildProject.setSeriesId(seriesId);
		boolean update = updateById(buildProject);
		if (update) {
			List<String> udIds = new ArrayList<>(Arrays.asList(userDoorwayId.split(",")));
			List<UserDoorway> list = userDoorwayService.list("id", SqlEmnus.IN, udIds, "id", "type", "work_out");
			list.forEach(ud -> {
				if (UserDoorway.TYPE_DOORWAY != ud.getType()) {
					ud.setType(UserDoorway.TYPE_PROJECT);
				}
			});

			userDoorwayService.updateBatchById(list);

			return R.ok(seriesId);
		}

		return R.error();
	}

	/**
	 * @dec 根据月份查询数据
	 * @date Mar 10, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> findByMonth(Map<String, String> params) {
		return baseMapper.findByMonth(params);
	}

	/**
	 * @dec 获取月份选项卡
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param buildEnginerId
	 * @return
	 */
	@Override
	public List<Integer> option(String buildEnginerId) {
		return baseMapper.option(buildEnginerId);
	}

	/**
	 * @dec 查看项目
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param params
	 * @param token
	 * @return
	 */
	@Override
	public List<Map<String, Object>> view(Map<String, Object> params, String token) {
		List<Map<String, Object>> list = queryList(params);
		if (CollUtil.isEmpty(list)) {
			return new ArrayList<>(0);
		}

		JSONObject jo = CommonUtil.decodeToken(token);
		String doorwayId = MapUtil.getStr(params, "doorwayId");
		QueryWrapper<UserDoorway> udc = new QueryWrapper<>();
		udc.eq("type", UserDoorway.TYPE_DOORWAY).and(i -> i.eq("doorway_id", doorwayId))
				.and(i -> i.eq("user_id", jo.getStr("id")));
		int doingCount = userDoorwayService.count(udc);

		udc = new QueryWrapper<>();
		udc.eq("doorway_id", doorwayId).and(i -> i.eq("user_id", jo.getStr("id"))).select("id");
		UserDoorway one = userDoorwayService.getOne(udc);

		return filterDatas(list, doingCount, one);
	}

	/**
	 * @dec 过滤数据
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> filterDatas(List<Map<String, Object>> list, int doingCount, UserDoorway one) {
		LocalDate now = LocalDate.now();

		List<Map<String, Object>> datas = list.stream().map(bp -> {
			Map<String, Object> data = new HashMap<>(16);
			data.put("id", MapUtil.getStr(bp, "id"));
			data.put("sort", MapUtil.getStr(bp, "sort"));
			data.put("name", MapUtil.getStr(bp, "name"));

			String beginDate = MapUtil.getStr(bp, "begin_date");
			String endDate = MapUtil.getStr(bp, "end_date");
			LocalDate begin = LocalDate.parse(beginDate);
			LocalDate end = LocalDate.parse(endDate);
			data.put("beginDate", beginDate.replaceAll("-", "."));
			data.put("endDate", endDate.replaceAll("-", "."));

			if (now.getYear() == begin.getYear()) {
				data.replace("beginDate", begin.format(DateTimeFormatter.ofPattern("MM.dd")));
			}
			if (now.getYear() == end.getYear()) {
				data.replace("endDate", end.format(DateTimeFormatter.ofPattern("MM.dd")));
			}

			data.put("type", MapUtil.getInt(bp, "type"));
			if (BuildProject.TYPE_OUT == MapUtil.getInt(bp, "type") && doingCount > 0) {
				data.put("delProject", true);
			} else {
				data.put("delProject", false);
			}

			String seriesId = MapUtil.getStr(bp, "series_id");
			data.put("pushLog", false);
			if (doingCount > 0) {
				data.replace("pushLog", true);
			} else {
				if (one != null && StrUtil.isNotBlank(seriesId)) {
					List<String> seriesIds = new ArrayList<>(Arrays.asList(seriesId.split(",")));
					if (seriesIds.contains(one.getId())) {
						data.replace("pushLog", true);
					}
				}
			}

			Integer planDays = MapUtil.getInt(bp, "plan_days");
			data.put("planDays", planDays);
			Integer status = MapUtil.getInt(bp, "status");
			data.put("status", status);

			String actualDate = MapUtil.getStr(bp, "actual_date");
			data.put("early", MapUtil.getInt(bp, "early"));
			long actualDay;

			QueryWrapper<BuildProjectLog> condition = new QueryWrapper<>();
			condition.eq("build_project_id", MapUtil.getStr(bp, "id"));
			condition.and(i -> i.lt("create_time", now.plusDays(1).toString() + " 00:00:00"));
			int count = buildProjectLogService.count(condition);
			if (count <= 0) {
				actualDay = 0;
			} else {
				if (StrUtil.isBlank(actualDate) && BuildProject.STATUS_DOING == status) {
					if (begin.isBefore(now) || begin.isEqual(now)) {
						actualDay = Math.abs(ChronoUnit.DAYS.between(begin, LocalDate.now())) + 1;
					} else {
						actualDay = -1;
					}
				} else if (StrUtil.isBlank(actualDate)) {
					actualDay = ChronoUnit.DAYS.between(begin, LocalDate.now());
				} else {
					LocalDate actual = LocalDate.parse(actualDate);
					if (actual.isBefore(end) || actual.isEqual(end)) {
						actualDay = ChronoUnit.DAYS.between(begin, end) + 1;
					} else {
						actualDay = ChronoUnit.DAYS.between(begin, actual) + 1;
					}
				}
				if (actualDay < 0) {
					actualDay = 0;
				} else if (actualDay == 0) {
					actualDay += 1;
				}
			}
			data.put("actualDays", actualDay);

			data.put("seriesName", "");
			data.put("seriesMobile", "");
			if (StrUtil.isNotBlank(seriesId)) {
				seriesId = seriesId.split(",")[0];
				UserDoorway userDoorway = userDoorwayService.getOne("id", SqlEmnus.EQ, seriesId);
				if (userDoorway != null) {
					data.replace("seriesName", userDoorway.getName());
					data.replace("seriesMobile", userDoorway.getMobile());
				}
			}

			data.put("address", "");
			String roomId = MapUtil.getStr(bp, "room_id");
			if (StrUtil.isNotBlank(roomId)) {
				Room room = roomService.getById(roomId);
				if (room != null) {
					Floor floor = floorService.getById(room.getFloorId());
					Mansion mansion = mansionService.getById(floor.getMansionId());
					data.replace("address", mansion.getName() + floor.getName() + room.getName());
				}
			}

			int overdueDays = MapUtil.getInt(bp, "overdue_days") != null ? MapUtil.getInt(bp, "overdue_days") : 0;
			data.put("overdueDays", overdueDays);

			return data;
		}).collect(Collectors.toList());

		return datas;
	}

	/**
	 * @dec 过滤数据
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param bpls
	 * @return
	 */
	@Override
	public List<Map<String, Object>> filterData(List<BuildProjectLog> list) {
		List<Map<String, Object>> collect = list.stream().map(bpl -> {
			Map<String, Object> data = new HashMap<>(8);
			User user = userService.getOne("id", SqlEmnus.EQ, bpl.getUserId(), "avatar", "real_name");
			data.put("realName", user.getRealName());
			data.put("avatar", user.getAvatar());
			data.put("remark", bpl.getRemark());
			data.put("early", bpl.getEarly());
			if (StrUtil.isNotBlank(bpl.getRoomId())) {
				Room room = roomService.getById(bpl.getRoomId());
				if (room != null) {
					Floor floor = floorService.getById(room.getFloorId());
					Mansion mansion = mansionService.getById(floor.getMansionId());
					data.put("address", mansion.getName() + floor.getName() + room.getName());
				}
			} else {
				data.put("address", "");
			}

			List<Map<String, Object>> files = new ArrayList<>();
			files.addAll(getVideo(bpl.getId()));
			List<String> images = getImages(bpl.getId());
			images.forEach(s -> {
				Map<String, Object> im = new HashMap<>(1);
				im.put("fileUrl", s);
				files.add(im);
			});
			data.put("audio", getAudio(bpl.getId()));
			data.put("files", files);

			LocalDateTime now = LocalDateTime.now();
			LocalDateTime createTime = bpl.getCreateTime();
			if (now.getYear() == createTime.getYear()) {
				data.put("time", createTime.format(DateTimeFormatter.ofPattern("MM.dd HH:mm")));
			} else {
				data.put("time", createTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
			}

			return data;
		}).collect(Collectors.toList());

		return collect;
	}

	/**
	 * @dec 获取工序列表
	 * @date Mar 27, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> queryV2(Map<String, Object> params) {
		return baseMapper.queryV2(params);
	}

	/**
	 * @dec 删除工序组长
	 * @date Mar 28, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param userDoorwayId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R delSernice(String id, String userDoorwayId) {
		BuildProject buildProject = getById(id);
		String seriesId = buildProject.getSeriesId();
		if (StrUtil.isNotBlank(seriesId)) {
			String[] seriesIds = seriesId.split(",");
			if (seriesIds.length > 1) {
				userDoorwayId = "," + userDoorwayId;
			}
			seriesId = seriesId.replaceAll(userDoorwayId, "");
		}
		buildProject.setSeriesId(seriesId);
		boolean update = updateById(buildProject);
		if (update) {
			UserDoorway userDoorway = userDoorwayService.getOne("id", SqlEmnus.EQ, userDoorwayId, "id", "type",
					"work_out");
			if (userDoorway != null) {
				if (UserDoorway.TYPE_DOORWAY != userDoorway.getType()) {
					userDoorway.setType(UserDoorway.TYPE_NORMAL);
					userDoorwayService.updateById(userDoorway);
				}
			}

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 获取日历数据
	 * @date Apr 1, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> queryCalendar(Map<String, Object> params) {
		return baseMapper.queryCalendar(params);
	}

}
