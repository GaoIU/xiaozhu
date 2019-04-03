package com.suizhu.work.doorway.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.common.constant.HttpConstant;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.FdfsClient;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.common.exception.ForbiddenException;
import com.suizhu.work.async.service.AsnycService;
import com.suizhu.work.build.service.BuildEnginerService;
import com.suizhu.work.build.service.BuildProjectLogService;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.build.service.BuildTipService;
import com.suizhu.work.condition.build.BuildCondition;
import com.suizhu.work.config.MyContansConfig;
import com.suizhu.work.doorway.mapper.DoorwayMapper;
import com.suizhu.work.doorway.service.DoorwayCategoryService;
import com.suizhu.work.doorway.service.DoorwayModelService;
import com.suizhu.work.doorway.service.DoorwayService;
import com.suizhu.work.entity.BuildEnginer;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildProjectLog;
import com.suizhu.work.entity.BuildTip;
import com.suizhu.work.entity.Doorway;
import com.suizhu.work.entity.DoorwayCategory;
import com.suizhu.work.entity.DoorwayModel;
import com.suizhu.work.entity.FileImage;
import com.suizhu.work.entity.Floor;
import com.suizhu.work.entity.LogDoorway;
import com.suizhu.work.entity.Mansion;
import com.suizhu.work.entity.Organization;
import com.suizhu.work.entity.Room;
import com.suizhu.work.entity.UserBuildEnginer;
import com.suizhu.work.entity.UserDoorway;
import com.suizhu.work.entity.UserOrg;
import com.suizhu.work.file.service.FileImageService;
import com.suizhu.work.log.service.LogDoorwayService;
import com.suizhu.work.mansion.service.FloorService;
import com.suizhu.work.mansion.service.MansionService;
import com.suizhu.work.mansion.service.RoomService;
import com.suizhu.work.model.service.ModelService;
import com.suizhu.work.user.service.OrganizationService;
import com.suizhu.work.user.service.UserBuildEnginerService;
import com.suizhu.work.user.service.UserDoorwayService;
import com.suizhu.work.user.service.UserService;
import com.suizhu.work.util.CalendarDate;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 门店表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@Service
@AllArgsConstructor
public class DoorwayServiceImpl extends ServiceImpl<DoorwayMapper, Doorway> implements DoorwayService {

	private final UserDoorwayService userDoorwayService;

	private final BuildEnginerService buildEnginerService;

	private final BuildProjectService buildProjectService;

	private final MyContansConfig myContansConfig;

	private final UserService userService;

	private final UserBuildEnginerService userBuildEnginerService;

	private static final String[] ALWAY_CITYS = { "北京市", "天津市", "重庆市", "上海市" };

	private final DoorwayCategoryService doorwayCategoryService;

	private final DoorwayModelService doorwayModelService;

	private final ModelService modelService;

	private final FileImageService fileImageService;

	private final FdfsClient fdfsClient;

	private final AsnycService asnycService;

	private final LogDoorwayService logDoorwayService;

	private final RoomService roomService;

	private final FloorService floorService;

	private final MansionService mansionService;

	private final BuildTipService buildTipService;

	private final BuildProjectLogService buildProjectLogService;

	private final OrganizationService organizationService;

	/**
	 * @dec 获取门店列表
	 * @date Mar 4, 2019
	 * @author gaochao
	 * @param params
	 * @param json
	 * @param userOrg
	 * @return
	 */
	@Override
	public R queryList(Map<String, Object> params, JSONObject json, UserOrg userOrg) {
		String city = MapUtil.getStr(params, "city");// 城市
		Double lat = MapUtil.getDouble(params, "lat");// 纬度
		Double lng = MapUtil.getDouble(params, "lng");// 经度
		String categoryId = MapUtil.getStr(params, "categoryId");// 分类ID
		Integer status = MapUtil.getInt(params, "status");// 施工
		Integer ep = MapUtil.getInt(params, "ep");// 异常

		Set<String> doorwayIds = new HashSet<>();
		boolean hasLook = false;
		if (userOrg != null) {
			List<Doorway> list = list("org_id", SqlEmnus.EQ, userOrg.getOrgId(), "id");
			if (CollUtil.isEmpty(list)) {
				return R.ok(new ArrayList<>(0));
			}

			doorwayIds = list.stream().map(Doorway::getId).collect(Collectors.toSet());
			hasLook = true;
		}

		if (!hasLook) {
			List<UserDoorway> list = userDoorwayService.list("user_id", SqlEmnus.EQ, json.getStr("id"), "doorway_id");
			if (CollUtil.isEmpty(list)) {
				return R.ok(new ArrayList<>(0));
			}

			doorwayIds = list.stream().map(UserDoorway::getDoorwayId).collect(Collectors.toSet());
			hasLook = true;
		}

		if (hasLook) {
			QueryWrapper<Doorway> queryWrapper = new QueryWrapper<>();
			if (StrUtil.isNotBlank(categoryId)) {
				queryWrapper.and(i -> i.eq("category_id", categoryId));
			}
			if (status != null) {
				queryWrapper.and(i -> i.eq("status", status));
			}
			if (ep != null) {
				List<BuildEnginer> buildEnginers = buildEnginerService.list("doorway_id", SqlEmnus.IN, doorwayIds,
						"id");
				if (CollUtil.isNotEmpty(buildEnginers)) {
					Set<String> buildEnginerIds = buildEnginers.stream().map(BuildEnginer::getId)
							.collect(Collectors.toSet());
					QueryWrapper<BuildProject> bpqw = new QueryWrapper<>();
					bpqw.in("build_enginer_id", buildEnginerIds);
					if (ep == Doorway.EP_EARLY) {
						bpqw.and(b -> b.eq("early", BuildProject.EARLY_WARNING));
					} else if (ep == Doorway.EP_OVERTIME) {
						bpqw.and(b -> b.eq("early", BuildProject.EARLY_OVERTIME_DOING));
					}
					bpqw.select("build_enginer_id");
					buildEnginerIds = buildProjectService.list(bpqw).stream().map(BuildProject::getBuildEnginerId)
							.collect(Collectors.toSet());

					if (CollUtil.isEmpty(buildEnginerIds)) {
						doorwayIds = new HashSet<>(0);
					} else {
						doorwayIds = buildEnginerService.list("id", SqlEmnus.IN, buildEnginerIds, "doorway_id").stream()
								.map(BuildEnginer::getDoorwayId).collect(Collectors.toSet());
					}
				}
			}

			if (CollUtil.isEmpty(doorwayIds)) {
				return R.ok(new ArrayList<>(0));
			}

			Set<String> ids = doorwayIds;
			queryWrapper.and(i -> i.in("id", ids));
			List<Doorway> list = list(queryWrapper);
			if (CollUtil.isNotEmpty(list)) {
				list.forEach(d -> {
					Organization organization = organizationService.getOne("id", SqlEmnus.EQ, d.getOrgId(),
							"expire_time");
					if (organization.getExpireTime().isBefore(LocalDate.now())) {
						list.remove(d);
					}
				});

				List<String> citys = sortCity(city, list);
				List<Map<String, Object>> datas = citys.stream().map(c -> {
					Map<String, Object> data = new HashMap<>(2);
					data.put("city", c);

					List<Doorway> doorway = new ArrayList<>();
					list.forEach(d -> {
						if (StrUtil.equals(c, d.getCity())) {
							doorway.add(d);
						}
					});
					data.put("doorways", sortDoorway(lat, lng, doorway));

					return data;
				}).collect(Collectors.toList());

				return R.ok(datas);
			}
		}

		return R.ok(new ArrayList<>(0));
	}

	/**
	 * @dec 排序门店
	 * @date Mar 4, 2019
	 * @author gaochao
	 * @param lat
	 * @param lng
	 * @param list
	 */
	private List<Map<String, Object>> sortDoorway(Double lat, Double lng, List<Doorway> list) {
		List<Map<String, Object>> collect = list.stream().sorted((c1, c2) -> {
			if (c1.getStatus() == Doorway.STATUS_DOING) {
				return -1;
			} else if (lat != null && lng != null) {
				String city1 = c1.getCity();
				if (ArrayUtil.contains(ALWAY_CITYS, city1)) {
					city1 = "";
				}
				String c1Address = c1.getProvince() + city1 + c1.getArea();
				String mapGeocoderUrl = String.format(myContansConfig.getMapGeocoderUrl(), c1Address,
						myContansConfig.getMapAk());
				String body1 = HttpUtil.get(mapGeocoderUrl, 5000);

				String city2 = c2.getCity();
				if (ArrayUtil.contains(ALWAY_CITYS, city2)) {
					city2 = "";
				}
				String c2Address = c2.getProvince() + city2 + c2.getArea();
				mapGeocoderUrl = String.format(myContansConfig.getMapGeocoderUrl(), c2Address,
						myContansConfig.getMapAk());
				String body2 = HttpUtil.get(mapGeocoderUrl, 5000);

				if (JSONUtil.isJson(body1) && JSONUtil.isJson(body2)) {
					JSONObject parseObj1 = JSONUtil.parseObj(body1);
					JSONObject parseObj2 = JSONUtil.parseObj(body2);
					if (parseObj1.getInt("status") == 0 && parseObj2.getInt("status") == 0) {
						JSONObject location1 = parseObj1.getJSONObject("result").getJSONObject("location");
						Double lat1 = location1.getDouble("lat");// 纬度
						Double lng1 = location1.getDouble("lng");// 经度

						JSONObject location2 = parseObj2.getJSONObject("result").getJSONObject("location");
						Double lat2 = location2.getDouble("lat");// 纬度
						Double lng2 = location2.getDouble("lng");// 经度

						String origins = lat + "," + lng;
						String destinations1 = lat1 + "," + lng1;
						String mapRoutematrixUrl = String.format(myContansConfig.getMapRoutematrixUrl(), origins,
								destinations1, myContansConfig.getMapAk());
						String rouBody1 = HttpUtil.get(mapRoutematrixUrl, 5000);

						String destinations2 = lat2 + "," + lng2;
						mapRoutematrixUrl = String.format(myContansConfig.getMapRoutematrixUrl(), origins,
								destinations2, myContansConfig.getMapAk());
						String rouBody2 = HttpUtil.get(mapRoutematrixUrl, 5000);

						if (JSONUtil.isJson(rouBody1) && JSONUtil.isJson(rouBody2)) {
							JSONObject rouParseObj1 = JSONUtil.parseObj(rouBody1);
							JSONObject rouParseObj2 = JSONUtil.parseObj(rouBody2);
							if (rouParseObj1.getInt("status") == 0 && rouParseObj2.getInt("status") == 0) {
								String result1 = rouParseObj1.getJSONArray("result").get(0).toString();
								JSONObject resObj1 = JSONUtil.parseObj(result1);
								JSONObject distance1 = resObj1.getJSONObject("distance");
								Double double1 = distance1.getDouble("value");

								String result2 = rouParseObj2.getJSONArray("result").get(0).toString();
								JSONObject resObj2 = JSONUtil.parseObj(result2);
								JSONObject distance2 = resObj2.getJSONObject("distance");
								Double double2 = distance2.getDouble("value");

								return double1.compareTo(double2);
							}
						}
					}
				}
			}
			return c1.getName().compareTo(c2.getName());
		}).collect(Collectors.toList()).stream().map(d -> {
			Map<String, Object> data = new HashMap<>(8);
			data.put("id", d.getId());
			data.put("name", d.getName());
			data.put("categoryId", d.getCategoryId());
			String name = doorwayCategoryService.getOne("id", SqlEmnus.EQ, d.getCategoryId(), "name").getName();
			data.put("categoryName", name);

			BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, d.getId(), "id");
			if (buildEnginer != null) {
				List<String> udIds = userBuildEnginerService
						.list("build_enginer_id", SqlEmnus.EQ, buildEnginer.getId(), "user_doorway_id").stream()
						.map(UserBuildEnginer::getUserDoorwayId).collect(Collectors.toList());
				QueryWrapper<UserDoorway> udqw = new QueryWrapper<>();
				udqw.in("id", udIds).orderByAsc("create_time").select("name", "user_id");
				UserDoorway userDoorway = userDoorwayService.list(udqw).get(0);
				data.put("realName", userDoorway.getName());
				if (StrUtil.isBlank(d.getCover())) {
					QueryWrapper<FileImage> wrapper = new QueryWrapper<>();
					wrapper.eq("doorway_id", d.getId()).and(i -> i.eq("type", FileImage.TYPE_ABLUM));
					wrapper.and(i -> i.eq("status", FileImage.STATUS_PERM)).orderByAsc("update_time")
							.select("file_url");
					List<FileImage> fis = fileImageService.list(wrapper);
					if (CollUtil.isNotEmpty(fis)) {
						data.put("avatar", fis.get(0).getFileUrl());
					} else {
						data.put("avatar", "");
					}
				} else {
					data.put("avatar", d.getCover());
				}

				QueryWrapper<BuildProject> bpqw = new QueryWrapper<>();
				bpqw.eq("build_enginer_id", buildEnginer.getId());
				bpqw.and(i -> i.eq("early", BuildProject.EARLY_WARNING));
				int early = buildProjectService.count(bpqw);
				data.put("early", early);

				bpqw = new QueryWrapper<>();
				bpqw.eq("build_enginer_id", buildEnginer.getId());
				bpqw.and(i -> i.eq("early", BuildProject.EARLY_OVERTIME_DOING));
				int overtime = buildProjectService.count(bpqw);
				data.put("overtime", overtime);

				bpqw = new QueryWrapper<>();
				bpqw.eq("build_enginer_id", buildEnginer.getId());
				bpqw.and(i -> i.eq("type", BuildProject.TYPE_IN));
				int sumCount = buildProjectService.count(bpqw);
				data.put("sumCount", sumCount);

				bpqw.and(i -> i.eq("status", BuildProject.STATUS_FLUSH));
				int count = buildProjectService.count(bpqw);
				data.put("count", count);
			} else {
				data.put("realName", "");
				data.put("avatar", "");
				data.put("early", 0);
				data.put("overtime", 0);
				data.put("sumCount", 0);
				data.put("count", 0);
			}
			return data;
		}).collect(Collectors.toList());

		return collect;
	}

	/**
	 * @dec 根据城市排序
	 * @date Mar 4, 2019
	 * @author gaochao
	 * @param city
	 * @param list
	 * @return
	 */
	private List<String> sortCity(String city, List<Doorway> list) {
		Set<String> set;
		if (StrUtil.isBlank(city)) {
			set = list.stream().map(Doorway::getCity).collect(Collectors.toSet());
			return new ArrayList<>(set);
		} else {
			set = list.stream().map(Doorway::getCity).collect(Collectors.toSet());
			return set.stream().sorted((c1, c2) -> {
				if (StrUtil.equals(c1, city)) {
					return -1;
				} else {
					return c1.compareTo(c2);
				}
			}).collect(Collectors.toList());
		}
	}

	/**
	 * @dec 新增门店
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param doorway
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R addDoorway(Doorway doorway) {
		boolean save = save(doorway);
		if (save) {
			String[] modelIds = doorway.getModelIds().split(",");
			List<DoorwayModel> dms = new ArrayList<>(modelIds.length);
			Stream.of(modelIds).forEach(mid -> {
				DoorwayModel model = new DoorwayModel(doorway.getId(), doorway.getCategoryId(), mid);
				dms.add(model);
			});

			doorwayModelService.saveBatch(dms);

			return R.ok(doorway.getId());
		}

		return R.error();
	}

	/**
	 * @dec 获取门店工程项目信息
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param id
	 * @param categoryId
	 * @param type
	 * @param json
	 * @param userOrg
	 * @return
	 */
	@Override
	public R getInfo(String id, String categoryId, Integer type, JSONObject jo, UserOrg userOrg) {
		DoorwayCategory doorwayCategory = doorwayCategoryService.getById(categoryId);
		if (DoorwayCategory.VAL_BUILD == doorwayCategory.getVal()) {
			BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, id, "id");
			if (buildEnginer == null) {
				return R.error(HttpConstant.FOUND, "请先录入工程信息！");
			}

			Map<String, Object> result = new HashMap<>(9);
			List<String> userDoorwayIds = userBuildEnginerService
					.list("build_enginer_id", SqlEmnus.EQ, buildEnginer.getId(), "user_doorway_id").stream()
					.map(UserBuildEnginer::getUserDoorwayId).collect(Collectors.toList());

			List<Map<String, Object>> engins = userDoorwayService
					.list("id", SqlEmnus.IN, userDoorwayIds, "name", "mobile").stream().map(ud -> {
						Map<String, Object> data = new HashMap<>(2);
						data.put("realName", ud.getName());
						data.put("mobile", ud.getMobile());
						return data;
					}).collect(Collectors.toList());
			result.put("engins", engins);
			result.put("buildEnginerId", buildEnginer.getId());

			QueryWrapper<BuildTip> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("user_id", jo.getStr("id")).and(i -> i.eq("build_enginer_id", buildEnginer.getId()));
			queryWrapper.select("remind");
			Integer remind = buildTipService.getOne(queryWrapper).getRemind();
			result.put("remind", remind);

			Map<String, Object> params = new HashMap<>(4);
			params.put("buildEnginerId", buildEnginer.getId());
			params.put("type", type);
			UserDoorway userDoorway = userDoorwayService.getOne("user_id", SqlEmnus.EQ, jo.getStr("id"), "id", "type");
			if (userOrg == null && userDoorway != null && UserDoorway.TYPE_DOORWAY != userDoorway.getType()) {
				params.put("seriesId", userDoorway.getId());
			}

			QueryWrapper<UserDoorway> udc = new QueryWrapper<>();
			udc.eq("type", UserDoorway.TYPE_DOORWAY).and(i -> i.eq("doorway_id", id))
					.and(i -> i.eq("user_id", jo.getStr("id")));
			int doingCount = userDoorwayService.count(udc);
			result.put("saveProject", false);
			result.put("doorwaySet", false);
			result.put("setSeries", false);
			if (doingCount > 0) {
				result.replace("saveProject", true);
				result.replace("doorwaySet", true);
				result.replace("setSeries", true);
			} else {
				udc = new QueryWrapper<>();
				udc.eq("doorway_id", id).and(i -> i.eq("user_id", jo.getStr("id")));
				doingCount = userDoorwayService.count(udc);
				if (doingCount > 0) {
					result.replace("doorwaySet", true);
				}
			}

			udc = new QueryWrapper<>();
			udc.eq("doorway_id", id).and(i -> i.eq("user_id", jo.getStr("id"))).select("id");
			UserDoorway one = userDoorwayService.getOne(udc);

			result.put("nextData", new ArrayList<>(0));
			if (type == 1 || type == 2) {
				List<Map<String, Object>> nextData = buildProjectService.nextQuery(params);
				params.put("doing", BuildProject.STATUS_DOING);
				result.replace("nextData", filterDatas(nextData, doingCount, one));
			}

			List<Map<String, Object>> data = buildProjectService.queryList(params);
			result.put("data", filterDatas(data, doingCount, one));

			params.put("bpIn", BuildProject.TYPE_IN);
			int sumCount = buildProjectService.queryCount(params);
			params.put("status", BuildProject.STATUS_FLUSH);
			int count = buildProjectService.queryCount(params);
			result.put("sumCount", sumCount);
			result.put("count", count);

			return R.ok(result);
		}

		return R.error();
	}

	/**
	 * @dec 过滤数据
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> filterDatas(List<Map<String, Object>> list, int dongCount, UserDoorway one) {
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
				beginDate = begin.format(DateTimeFormatter.ofPattern("MM.dd"));
				data.replace("beginDate", beginDate);
			}
			if (now.getYear() == end.getYear()) {
				endDate = end.format(DateTimeFormatter.ofPattern("MM.dd"));
				data.replace("endDate", endDate);
			}

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

			data.put("type", MapUtil.getInt(bp, "type"));
			if (BuildProject.TYPE_OUT == MapUtil.getInt(bp, "type") && dongCount > 0) {
				data.put("delProject", true);
			} else {
				data.put("delProject", false);
			}

			String seriesId = MapUtil.getStr(bp, "series_id");
			data.put("pushLog", false);
			if (dongCount > 0) {
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

			data.put("seriesMobile", "");
			data.put("seriesName", "");
			if (StrUtil.isNotBlank(seriesId)) {
				seriesId = seriesId.split(",")[0];
				UserDoorway userDoorway = userDoorwayService.getOne("id", SqlEmnus.EQ, seriesId, "id", "name",
						"mobile");
				if (userDoorway != null) {
					data.replace("seriesMobile", userDoorway.getMobile());
					data.replace("seriesName", userDoorway.getName());
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
	 * @dec 获取日历
	 * @date Mar 10, 2019
	 * @author gaochao
	 * @param id
	 * @param categoryId
	 * @param year
	 * @param month
	 * @return
	 */
	@Override
	public R getCalendar(String id, String categoryId, Integer year, Integer month) {
		DoorwayCategory doorwayCategory = doorwayCategoryService.getById(categoryId);
		if (DoorwayCategory.VAL_BUILD == doorwayCategory.getVal()) {
			BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, id, "id");
			if (buildEnginer == null) {
				return R.error(HttpConstant.FOUND, "请先录入工程信息！");
			}

			Map<String, String> params = new HashMap<>(2);
			params.put("buildEnginerId", buildEnginer.getId());
			params.put("date", year + "-" + (month < 10 ? "0" + month : month));
			List<Map<String, Object>> list = buildProjectService.findByMonth(params);

			List<CalendarDate> cdList = new ArrayList<>();
			int monthDays = CommonUtil.monthDays(year, month);
			for (int i = 1; i <= monthDays; i++) {
				LocalDate date = LocalDate.of(year, month, i);
				int dayOfWeek = CommonUtil.dayOfWeek(date);
				boolean isToday = CommonUtil.isToday(date);

				Map<String, Integer> info = new HashMap<>(3);
				int start = 0;
				int early = 0;
				int overtime = 0;
				if (CollUtil.isNotEmpty(list)) {
					for (Map<String, Object> bp : list) {
						if (StrUtil.equals(date.toString(), MapUtil.getStr(bp, "begin_date"))) {
							Integer earlyInt = MapUtil.getInt(bp, "early");
							Integer status = MapUtil.getInt(bp, "status");
							if (BuildProject.STATUS_NOT_START == status) {
								start++;
							}

							if (BuildProject.EARLY_WARNING == earlyInt && BuildProject.STATUS_DOING == status) {
								early++;
							} else if (BuildProject.EARLY_OVERTIME_DOING == earlyInt
									&& BuildProject.STATUS_DOING == status) {
								overtime++;
							}
						}
					}
				}
				info.put("start", start);
				info.put("early", early);
				info.put("overtime", overtime);

				CalendarDate calendarDate = CalendarDate.builder().day(i).weekDay(dayOfWeek).isToday(isToday).info(info)
						.build();
				cdList.add(calendarDate);
			}

			return R.ok(cdList);
		}

		return R.error();
	}

	/**
	 * @dec 查看门店概况
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@Override
	public R detail(String id, JSONObject json, int count) {
		Map<String, Object> result = new HashMap<>(4);
		QueryWrapper<UserDoorway> udc = new QueryWrapper<>();
		udc.eq("type", UserDoorway.TYPE_DOORWAY).and(i -> i.eq("doorway_id", id))
				.and(i -> i.eq("user_id", json.getStr("id")));
		int udCount = userDoorwayService.count(udc);
		if (count > 0 || udCount > 0) {
			result.put("edit", true);
		} else {
			result.put("edit", false);
		}

		if (udCount > 0) {
			result.put("imgEdit", true);
		} else {
			result.put("imgEdit", false);
		}

		Doorway doorway = getById(id);
		String[] dfls = { "orgId", "status", "createId", "createTime", "updateTime", "modelIds" };
		Map<String, Object> d = CommonUtil.beanToMapFl(doorway, dfls);

		DoorwayCategory category = doorwayCategoryService.getOne("id", SqlEmnus.EQ, doorway.getCategoryId(), "name",
				"val");
		d.put("categoryName", category.getName());

		QueryWrapper<DoorwayModel> dmqw = new QueryWrapper<>();
		dmqw.eq("doorway_id", id).and(i -> i.eq("category_id", doorway.getCategoryId())).select("model_id");
		List<String> modelIds = doorwayModelService.list(dmqw).stream().map(DoorwayModel::getModelId)
				.collect(Collectors.toList());
		List<Map<String, String>> models = modelService.list("id", SqlEmnus.IN, modelIds, "id", "name").stream()
				.map(m -> {
					Map<String, String> data = new HashMap<>(2);
					data.put("id", m.getId());
					data.put("name", m.getName());
					return data;
				}).collect(Collectors.toList());
		d.put("models", models);
		result.put("doorway", d);

		if (category.getVal() == DoorwayCategory.VAL_BUILD) {
			BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, id);
			if (buildEnginer != null) {
				String[] fls = { "doorwayId", "actualDate", "status", "createTime", "updateTime", "seriesMobile",
						"seriesName" };
				Map<String, Object> be = CommonUtil.beanToMapFl(buildEnginer, fls);
				be.replace("contractSignDate", buildEnginer.getContractSignDate().toString().replaceAll("-", "."));

				if (buildEnginer.getPrepareDate() != null) {
					be.replace("prepareDate", buildEnginer.getPrepareDate().toString().replaceAll("-", "."));
				}
				if (buildEnginer.getForecastDate() != null) {
					be.replace("forecastDate", buildEnginer.getForecastDate().toString().replaceAll("-", "."));
				}

				List<String> userDoorwayIds = userBuildEnginerService
						.list("build_enginer_id", SqlEmnus.EQ, buildEnginer.getId(), "user_doorway_id").stream()
						.map(UserBuildEnginer::getUserDoorwayId).collect(Collectors.toList());
				List<Map<String, String>> series = userDoorwayService
						.list("id", SqlEmnus.IN, userDoorwayIds, "name", "mobile").stream().map(ud -> {
							Map<String, String> data = new HashMap<>(2);
							data.put("seriesName", ud.getName());
							data.put("seriesMobile", ud.getMobile());
							return data;
						}).collect(Collectors.toList());
				be.put("series", series);

				String ownerName = buildEnginer.getOwnerName();
				String ownerMobile = buildEnginer.getOwnerMobile();
				String devName = buildEnginer.getDevName();
				String devMobile = buildEnginer.getDevMobile();
				be.replace("ownerName", ownerName.split(",")[0]);
				be.replace("ownerMobile", ownerMobile.split(",")[0]);
				be.replace("devName", devName.split(",")[0]);
				be.replace("devMobile", devMobile.split(",")[0]);

				result.put("buildEnginer", be);
			}
		}

		return R.ok(result);
	}

	/**
	 * @dec 获取图片列表
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param id
	 * @param type
	 * @param current
	 * @param size
	 * @return
	 */
	@Override
	public R fileImages(String id, Integer type, Integer current, Integer size) {
		QueryWrapper<FileImage> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("doorway_id", id).and(i -> i.eq("status", FileImage.STATUS_PERM));
		queryWrapper.orderByDesc("update_time");
		if (type == FileImage.TYPE_ABLUM) {
			queryWrapper.and(i -> i.eq("type", FileImage.TYPE_ABLUM).or(o -> o.eq("type", FileImage.TYPE_PROJECT)));
		} else {
			queryWrapper.and(i -> i.eq("type", type));
		}

		IPage<FileImage> page = new Page<>(current, size);
		List<FileImage> records = fileImageService.page(page, queryWrapper).getRecords();
		if (CollUtil.isEmpty(records)) {
			return R.ok(records);
		}

		List<Map<String, Object>> list = records.stream().map(fi -> {
			Map<String, Object> data = new HashMap<>(4);
			data.put("fileId", fi.getFileId());
			data.put("fileUrl", fi.getFileUrl());
			String realName = userService.getOne("id", SqlEmnus.EQ, fi.getUserId(), "real_name").getRealName();
			data.put("realName", realName);
			data.put("projectName", "");

			if (StrUtil.isNotBlank(fi.getBuildProjectLogId())) {
				BuildProjectLog buildProjectLog = buildProjectLogService.getOne("id", SqlEmnus.EQ,
						fi.getBuildProjectLogId(), "build_project_id");
				String name = buildProjectService.getOne("id", SqlEmnus.EQ, buildProjectLog.getBuildProjectId(), "name")
						.getName();
				data.replace("projectName", name);
			}

			return data;
		}).collect(Collectors.toList());

		return R.ok(list);
	}

	/**
	 * @dec 保存文件
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param uploadFileId
	 * @param delFileId
	 * @param type
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R updateFile(String uploadFileId, String delFileId, Integer type) {
		boolean remove = false;
		boolean update = false;

		if (StrUtil.isNotBlank(delFileId)) {
			List<String> delFileIds = new ArrayList<>(Arrays.asList(delFileId.split(",")));
			fdfsClient.delete(delFileIds);
			remove = fileImageService.remove("file_id", SqlEmnus.IN, delFileIds);
		}

		if (StrUtil.isNotBlank(uploadFileId)) {
			List<String> fileIds = new ArrayList<>(Arrays.asList(uploadFileId.split(",")));
			List<FileImage> list = fileImageService.list("file_id", SqlEmnus.IN, fileIds);
			list.forEach(fi -> {
				fi.setStatus(FileImage.STATUS_PERM);
				fi.setType(type);
			});

			update = fileImageService.updateBatchById(list);
		}

		if (remove || update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改门店
	 * @date Mar 6, 2019
	 * @author gaochao
	 * @param doorway
	 * @param json
	 * @param userOrg
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R edit(Doorway doorway, JSONObject json, UserOrg userOrg) {
		boolean hasEdit = false;
		if (userOrg != null) {
			hasEdit = true;
		}

		if (!hasEdit) {
			QueryWrapper<UserDoorway> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("doorway_id", doorway.getId()).and(i -> i.eq("user_id", json.getStr("id")));
			int count = userDoorwayService.count(queryWrapper);
			if (count > 0) {
				hasEdit = true;
			}
		}

		if (!hasEdit) {
			throw new ForbiddenException();
		}

		DoorwayCategory category = doorwayCategoryService.getOne("id", SqlEmnus.EQ, doorway.getCategoryId(), "val");
		List<String> modelIds = new ArrayList<>(Arrays.asList(doorway.getModelIds().split(",")));
		List<String> modelIdList = doorwayModelService.list("doorway_id", SqlEmnus.EQ, doorway.getId(), "model_id")
				.stream().map(DoorwayModel::getModelId).collect(Collectors.toList());

		if (category.getVal() == DoorwayCategory.VAL_BUILD) {
			if (!StrUtil.equals(modelIds.get(0), modelIdList.get(0))) {
				QueryWrapper<BuildEnginer> queryWrapper = new QueryWrapper<>();
				queryWrapper.eq("status", BuildEnginer.STATUS_DOING).and(i -> i.eq("doorway_id", doorway.getId()));
				int count = buildEnginerService.count(queryWrapper);
				if (count > 0) {
					return R.error("工程已经开始，无法修改模版！");
				}

				BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, doorway.getId());
				asnycService.editModel(buildEnginer, modelIds, json.getStr("id"));
			}
		}

		boolean update = updateById(doorway);
		if (update) {
			LogDoorway logDoorway = new LogDoorway(json.getStr("id"), doorway.getId(), LogDoorway.TYPE_EDIT);
			logDoorwayService.save(logDoorway);
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 查看楼层列表
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@Override
	public R mansion(String id, String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("doorway_id", id).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(queryWrapper);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		List<Mansion> list = mansionService.list("doorway_id", SqlEmnus.EQ, id, "id", "name");
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		List<Map<String, Object>> mansions = list.stream().map(m -> {
			Map<String, Object> data = new HashMap<>(3);
			data.put("id", m.getId());
			data.put("name", m.getName());
			data.put("floors", new ArrayList<>(0));
			List<Floor> floors = floorService.list("mansion_id", SqlEmnus.EQ, m.getId(), "id", "name");
			if (CollUtil.isNotEmpty(floors)) {
				List<Map<String, Object>> collect = floors.stream().map(f -> {
					Map<String, Object> fm = new HashMap<>(3);
					fm.put("id", f.getId());
					fm.put("name", f.getName());
					fm.put("rooms", new ArrayList<>(0));

					List<Room> rooms = roomService.list("floor_id", SqlEmnus.EQ, f.getId(), "id", "name");
					if (CollUtil.isNotEmpty(rooms)) {
						List<Map<String, Object>> rc = rooms.stream().map(r -> {
							Map<String, Object> rm = new HashMap<>(2);
							rm.put("id", r.getId());
							rm.put("name", r.getName());
							return rm;
						}).collect(Collectors.toList());
						fm.replace("rooms", rc);
					}
					return fm;
				}).collect(Collectors.toList());

				data.replace("floors", collect);
			}
			return data;
		}).collect(Collectors.toList());

		return R.ok(mansions);
	}

	/**
	 * @dec 获取人员管理列表
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param id
	 * @param dep
	 * @param workType
	 * @param token
	 * @return
	 */
	@Override
	public R user(String id, String dep, String workKind, String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("doorway_id", id).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(queryWrapper);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("doorway_id", id).and(i -> i.ne("type", UserDoorway.TYPE_DOORWAY));
		if (StrUtil.isNotBlank(dep)) {
			queryWrapper.and(i -> i.eq("dep", dep));
		}
		if (StrUtil.isNotBlank(workKind)) {
			queryWrapper.and(i -> i.eq("work_kind", workKind));
		}

		List<UserDoorway> list = userDoorwayService.list(queryWrapper);
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		List<Map<String, Object>> datas = list.stream().map(ud -> {
			Map<String, Object> data = new HashMap<>(9);
			data.put("id", ud.getId());
			data.put("mobile", ud.getMobile());
			data.put("name", ud.getName());
			data.put("workNum", ud.getWorkNum());
			data.put("email", ud.getEmail());
			data.put("dep", ud.getDep());
			data.put("pos", ud.getPos());
			data.put("workKind", ud.getWorkKind());
			data.put("workOut", ud.getWorkOut());
			return data;
		}).collect(Collectors.toList());

		return R.ok(datas);
	}

	/**
	 * @dec 获取人员管理选项卡
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@Override
	public R userOption(String id, String token) {
		Map<String, Object> data = new HashMap<>(2);

		QueryWrapper<UserDoorway> depWrapper = new QueryWrapper<>();
		depWrapper.eq("doorway_id", id).groupBy("dep").select("dep");
		List<UserDoorway> depList = userDoorwayService.list(depWrapper);
		if (CollUtil.isNotEmpty(depList)) {
			List<String> deps = depList.stream().map(UserDoorway::getDep).collect(Collectors.toList());
			data.put("deps", deps);
		}

		QueryWrapper<UserDoorway> workTypeWrapper = new QueryWrapper<>();
		workTypeWrapper.eq("doorway_id", id).groupBy("work_kind").select("work_kind");
		List<UserDoorway> workTypeList = userDoorwayService.list(workTypeWrapper);
		if (CollUtil.isNotEmpty(workTypeList)) {
			List<String> wts = workTypeList.stream().map(UserDoorway::getWorkKind).collect(Collectors.toList());
			data.put("wts", wts);
		}

		return R.ok(data);
	}

	/**
	 * @dec 获取人员管理列表
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param condition
	 * @param userDoorwayId
	 * @param token
	 * @return
	 */
	@Override
	public R userV2(String id, String condition, String userDoorwayId, String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		QueryWrapper<UserDoorway> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("doorway_id", id).and(i -> i.eq("user_id", json.getStr("id")));
		int count = userDoorwayService.count(queryWrapper);
		if (count <= 0) {
			throw new ForbiddenException();
		}

		queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("doorway_id", id).and(i -> i.ne("type", UserDoorway.TYPE_DOORWAY));
		if (StrUtil.isNotBlank(condition)) {
			queryWrapper.and(i -> i.like("work_kind", condition).or(m -> m.like("mobile", condition))
					.or(n -> n.like("name", condition)).or(wn -> wn.like("work_num", condition))
					.or(e -> e.like("email", condition)));
		}
		if (StrUtil.isNotBlank(userDoorwayId)) {
			List<String> udIds = new ArrayList<>(Arrays.asList(userDoorwayId.split(",")));
			queryWrapper.and(i -> i.notIn("id", udIds));
		}

		List<UserDoorway> list = userDoorwayService.list(queryWrapper);
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		Set<String> deps = list.stream().map(UserDoorway::getDep).collect(Collectors.toSet());
		List<Map<String, Object>> datas = new ArrayList<>(deps.size());
		deps.forEach(d -> {
			Map<String, Object> re = new HashMap<>(2);
			re.put("dep", d);

			List<Map<String, Object>> userDoorways = new ArrayList<>();
			list.forEach(ud -> {
				if (StrUtil.equals(ud.getDep(), d)) {
					Map<String, Object> data = new HashMap<>(8);
					data.put("id", ud.getId());
					data.put("mobile", ud.getMobile());
					data.put("name", ud.getName());
					data.put("workNum", ud.getWorkNum());
					data.put("email", ud.getEmail());
					data.put("pos", ud.getPos());
					data.put("workKind", ud.getWorkKind());
					data.put("workOut", ud.getWorkOut());
					userDoorways.add(data);
				}
			});

			re.put("userDoorways", userDoorways);

			datas.add(re);
		});

		return R.ok(datas);
	}

	/**
	 * @dec 获取门店工程项目信息
	 * @date Mar 26, 2019
	 * @author gaochao
	 * @since 2.1.0
	 * @param buildCondition
	 * @param json
	 * @param userOrg
	 * @return
	 */
	@Override
	public R info(BuildCondition buildCondition, JSONObject json, UserOrg userOrg) {
		DoorwayCategory doorwayCategory = doorwayCategoryService.getById(buildCondition.getCategoryId());
		if (DoorwayCategory.VAL_BUILD == doorwayCategory.getVal()) {
			BuildEnginer be = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, buildCondition.getId(), "id",
					"prepare_date", "forecast_date");
			if (be == null) {
				return R.error(HttpConstant.FOUND, "请先录入工程信息！");
			}

			Map<String, Object> result = new HashMap<>(9);
			String beId = be.getId();
			String userDoorwayId = userBuildEnginerService
					.list("build_enginer_id", SqlEmnus.EQ, beId, "user_doorway_id").get(0).getUserDoorwayId();
			UserDoorway userDoorway = userDoorwayService.getOne("id", SqlEmnus.EQ, userDoorwayId, "name", "mobile");
			result.put("name", userDoorway.getName());
			result.put("mobile", userDoorway.getMobile());
			result.put("buildEnginerId", beId);

			QueryWrapper<BuildTip> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("user_id", json.getStr("id")).and(i -> i.eq("build_enginer_id", beId));
			queryWrapper.select("remind");
			Integer remind = buildTipService.getOne(queryWrapper).getRemind();
			result.put("remind", remind);

			result.put("saveProject", false);
			result.put("doorwaySet", false);
			result.put("setSeries", false);

			Map<String, Object> params = new HashMap<>(6);
			params.put("buildEnginerId", be.getId());
			params.put("type", buildCondition.getType());
			params.put("bpIn", BuildProject.TYPE_IN);

			String udId = null;
			QueryWrapper<UserDoorway> wrapper = new QueryWrapper<>();
			wrapper.eq("user_id", json.getStr("id")).and(i -> i.eq("doorway_id", buildCondition.getId()));
			UserDoorway udser = userDoorwayService.getOne(wrapper);
			if (userOrg == null && udser != null) {
				result.replace("doorwaySet", true);
				params.put("seriesId", userDoorway.getId());
				udId = udser.getId();
			}
			wrapper.and(i -> i.eq("type", UserDoorway.TYPE_DOORWAY));
			int doorInt = userDoorwayService.count(wrapper);
			if (doorInt > 0) {
				result.replace("saveProject", true);
				result.replace("doorwaySet", true);
				result.replace("setSeries", true);
			}

			List<Map<String, Object>> list = getProjectList(buildCondition, beId, udId);
			result.put("list", filterList(list, doorInt, udId));

			List<Map<String, Object>> calendar = getCalendar(buildCondition, be, udId);
			result.put("calendar", calendar);

			int sumCount = buildProjectService.queryCount(params);
			result.put("sumCount", sumCount);

			params.put("status", BuildProject.STATUS_FLUSH);
			int count = buildProjectService.queryCount(params);
			result.put("count", count);

			return R.ok(result);
		}

		return R.error();
	}

	/**
	 * @dec 获取日历
	 * @date Mar 27, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param buildCondition
	 * @param be
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> getCalendar(BuildCondition buildCondition, BuildEnginer be, String udId) {
		List<Map<String, Object>> cdList = new ArrayList<>();
		Integer year = buildCondition.getYear();
		Integer month = buildCondition.getMonth();
		int monthDays = CommonUtil.monthDays(year, month);
		long week = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_MONTH);
		if (StrUtil.isNotBlank(buildCondition.getWeek())) {
			week = Integer.valueOf(buildCondition.getWeek());
		}
		week -= 1;

		Map<String, Object> params = new HashMap<>(8);
		params.put("buildEnginerId", be.getId());
		params.put("type", buildCondition.getType());
		params.put("doing", BuildProject.STATUS_DOING);
		if (StrUtil.isNotBlank(udId)) {
			params.remove("doing");
			params.put("seriesId", udId);
		}

		if (buildCondition.getType() != null && BuildCondition.TYPE_WEEK == buildCondition.getType()) {
			LocalDate plusWeeks = LocalDate.of(year, month, 1).plusWeeks(week);
			if (plusWeeks.getMonthValue() > month) {
				plusWeeks = plusWeeks.minusDays(1);
			}
			LocalDate start = plusWeeks.with(DayOfWeek.MONDAY);
			LocalDate end = plusWeeks.with(DayOfWeek.SUNDAY);
			params.put("beginDate", end.toString());
			LocalDate of = LocalDate.of(year, month, 1);
			List<Map<String, Object>> list = buildProjectService.queryCalendar(params);
			weekCal(start, of, monthDays, year, month, be, cdList, list);
		} else if (buildCondition.getType() != null && BuildCondition.TYPE_MONTH == buildCondition.getType()) {
			params.put("monthDate", year + "" + (month < 10 ? "0" + month : month));
			List<Map<String, Object>> list = buildProjectService.queryCalendar(params);
			monthCal(monthDays, year, month, be, cdList, list);
		} else {
			params.remove("doing");
			List<Map<String, Object>> list = buildProjectService.queryCalendar(params);
			monthCal(monthDays, year, month, be, cdList, list);
		}

		return cdList;
	}

	/**
	 * @dec 获取周日历
	 * @date Apr 1, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param start
	 * @param of
	 * @param monthDays
	 * @param year
	 * @param month
	 * @param be
	 * @param cdList
	 * @param list
	 */
	private void weekCal(LocalDate start, LocalDate of, int monthDays, int year, int month, BuildEnginer be,
			List<Map<String, Object>> cdList, List<Map<String, Object>> list) {
		for (int i = 0; i < 7; i++) {
			LocalDate date = start.plusDays(i);
			int dayOfWeek = CommonUtil.dayOfWeek(date); // 星期几
			boolean isToday = CommonUtil.isToday(date); // 是否是今天
			boolean isPreDate = date.isEqual(be.getPrepareDate()); // 是否是开始筹建日期
			boolean isForeDate = date.isEqual(be.getForecastDate()); // 是否是预计竣工日期
			long days = ChronoUnit.DAYS.between(of, date); // 这个月的第几天
			if (days >= 0) {
				days += 1;
				if (days > monthDays) {
					days -= monthDays;
				}
			} else if (days < 0) {
				LocalDate minusMonths = of.minusMonths(1);
				monthDays = CommonUtil.monthDays(minusMonths.getYear(), minusMonths.getMonthValue());
				days = monthDays + days + 1;
			}

			Map<String, Object> data = new HashMap<>(6);
			data.put("dayOfWeek", dayOfWeek);
			data.put("isToday", isToday);
			data.put("isPreDate", isPreDate);
			data.put("isForeDate", isForeDate);
			data.put("day", days);
			calendar(list, date, data);

			cdList.add(data);
		}
	}

	/**
	 * @dec 获取月日历
	 * @date Apr 1, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param monthDays
	 * @param year
	 * @param month
	 * @param be
	 * @param cdList
	 * @param list
	 */
	private void monthCal(int monthDays, int year, int month, BuildEnginer be, List<Map<String, Object>> cdList,
			List<Map<String, Object>> list) {
		for (int i = 1; i <= monthDays; i++) {
			LocalDate date = LocalDate.of(year, month, i);
			int dayOfWeek = CommonUtil.dayOfWeek(date); // 星期几
			boolean isToday = CommonUtil.isToday(date); // 是否是今天
			boolean isPreDate = date.isEqual(be.getPrepareDate()); // 是否是开始筹建日期
			boolean isForeDate = date.isEqual(be.getForecastDate()); // 是否是预计竣工日期

			Map<String, Object> data = new HashMap<>(6);
			data.put("dayOfWeek", dayOfWeek);
			data.put("isToday", isToday);
			data.put("isPreDate", isPreDate);
			data.put("isForeDate", isForeDate);
			data.put("day", i);
			calendar(list, date, data);

			cdList.add(data);
		}
	}

	/**
	 * @dec 设置日历
	 * @date Apr 1, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param list
	 * @param date
	 * @param data
	 */
	private void calendar(List<Map<String, Object>> list, LocalDate date, Map<String, Object> data) {
		Map<String, Integer> info = new HashMap<>(3);
		int startInt = 0;
		int earlyInt = 0;
		int overtimeInt = 0;
		LocalDate now = LocalDate.now();
		if (CollUtil.isNotEmpty(list)) {
			for (Map<String, Object> map : list) {
				LocalDate beginDate = LocalDate.parse(MapUtil.getStr(map, "begin_date"));
				LocalDate endDate = LocalDate.parse(MapUtil.getStr(map, "end_date")).plusDays(1);
				Integer early = MapUtil.getInt(map, "early");
				Integer status = MapUtil.getInt(map, "status");

				if (beginDate.isEqual(date)) {
					if (status == BuildProject.STATUS_NOT_START) {
						startInt++;
					}
					if (status == BuildProject.STATUS_DOING && early == BuildProject.EARLY_WARNING) {
						earlyInt++;
					}
				}

				if (beginDate.isBefore(date) && (date.isBefore(now) || date.isEqual(now))) {
					if (status == BuildProject.STATUS_DOING && early == BuildProject.EARLY_WARNING) {
						earlyInt++;
					}
				}

				if ((endDate.isBefore(date) || endDate.isEqual(date)) && (date.isBefore(now) || date.isEqual(now))) {
					if (status == BuildProject.STATUS_DOING && early == BuildProject.EARLY_OVERTIME_DOING) {
						overtimeInt++;
					}
					if (status == BuildProject.STATUS_FLUSH && early == BuildProject.EARLY_OVERTIME_FLUSH) {
						overtimeInt++;
					}
				}
			}

			for (Map<String, Object> map : list) {
				String actual_date = MapUtil.getStr(map, "actual_date");
				if (StrUtil.isNotBlank(actual_date)) {
					Integer early = MapUtil.getInt(map, "early");
					Integer status = MapUtil.getInt(map, "status");
					LocalDate actualDate = LocalDate.parse(actual_date);
					if (status == BuildProject.STATUS_FLUSH && early == BuildProject.EARLY_OVERTIME_FLUSH
							&& (date.isEqual(actualDate) || date.isAfter(actualDate))) {
						overtimeInt--;
					}
				}
			}
		}
		info.put("start", startInt);
		info.put("early", earlyInt);
		info.put("overtime", overtimeInt);
		data.put("info", info);
	}

	/**
	 * @dec 获取工序列表
	 * @date Mar 27, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param buildCondition
	 * @param beId
	 * @param bpIds
	 * @return
	 */
	public List<Map<String, Object>> getProjectList(BuildCondition buildCondition, String beId, String udId) {
		Integer year = buildCondition.getYear();
		Integer month = buildCondition.getMonth();
		LocalDate now = LocalDate.now();
		long week = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_MONTH);
		if (StrUtil.isNotBlank(buildCondition.getWeek())) {
			week = Integer.valueOf(buildCondition.getWeek());
		}
		week -= 1;

		Map<String, Object> params = new HashMap<>(8);
		params.put("buildEnginerId", beId);
		params.put("flush", BuildProject.STATUS_FLUSH);
		params.put("type", buildCondition.getType());
		params.put("doing", buildCondition.getType());
		if (buildCondition.getStatus() != null) {
			params.put("status", buildCondition.getStatus());
		}
		if (buildCondition.getDay() != null) {
			params.remove("doing");
			LocalDate of = LocalDate.of(year, month, buildCondition.getDay());
			params.put("date", of.toString());
			if (of.isBefore(now) || of.isEqual(now)) {
				params.put("gtDate", of.toString());
			}
		}
		if (StrUtil.isNotBlank(udId)) {
			params.put("seriesId", udId);
		}

		if (buildCondition.getType() != null && BuildCondition.TYPE_WEEK == buildCondition.getType()) {
			LocalDate plusWeeks = LocalDate.of(year, month, 1).plusWeeks(week);
			if (plusWeeks.getMonthValue() > month) {
				plusWeeks = plusWeeks.minusDays(1);
			}
			LocalDate start = plusWeeks.with(DayOfWeek.MONDAY);
			params.put("beginDate", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			if (start.isBefore(now) || start.isEqual(now)) {
				params.put("gtWeek", start.toString());
			}
			LocalDate end = plusWeeks.with(DayOfWeek.SUNDAY);
			params.put("endDate", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		} else if (buildCondition.getType() != null && BuildCondition.TYPE_MONTH == buildCondition.getType()) {
			params.put("monthDate", year + "" + (month < 10 ? "0" + month : month));
			if (year <= now.getYear() && month <= now.getMonthValue()) {
				params.put("gtMonth", month);
			}
		}

		return buildProjectService.queryV2(params);
	}

	/**
	 * @dec 过滤数据
	 * @date Mar 27, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param list
	 * @param doorInt
	 * @param bpIds
	 * @return
	 */
	private List<Map<String, Object>> filterList(List<Map<String, Object>> list, int doorInt, String udId) {
		LocalDate now = LocalDate.now();
		List<Map<String, Object>> collect = list.stream().map(bp -> {
			Map<String, Object> data = new HashMap<>(15);
			data.put("id", MapUtil.getStr(bp, "id"));
			data.put("name", MapUtil.getStr(bp, "name"));

			String beginDate = MapUtil.getStr(bp, "begin_date");
			String endDate = MapUtil.getStr(bp, "end_date");
			LocalDate begin = LocalDate.parse(beginDate);
			LocalDate end = LocalDate.parse(endDate);

			data.put("beginDate", begin.format(DateTimeFormatter.ofPattern("MM.dd")));
			data.put("endDate", end.format(DateTimeFormatter.ofPattern("MM.dd")));
			if (now.getYear() != begin.getYear()) {
				data.replace("beginDate", beginDate.replaceAll("-", "."));
			}
			if (now.getYear() != end.getYear()) {
				data.replace("endDate", endDate.replaceAll("-", "."));
			}

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
					actualDay = Math.abs(ChronoUnit.DAYS.between(begin, LocalDate.now())) + 1;
					if (begin.isAfter(now)) {
						actualDay = 0;
					}
				} else if (StrUtil.isBlank(actualDate)) {
					actualDay = 0;
				} else {
					LocalDate actual = LocalDate.parse(actualDate);
					if (actual.isBefore(end) || actual.isEqual(end)) {
						actualDay = ChronoUnit.DAYS.between(begin, end) + 1;
					} else {
						actualDay = Math.abs(ChronoUnit.DAYS.between(begin, actual)) + 1;
					}
				}
			}
			data.put("actualDays", actualDay);

			Integer type = MapUtil.getInt(bp, "type");
			data.put("type", type);
			if (BuildProject.TYPE_OUT == type && doorInt > 0) {
				data.put("delProject", true);
			} else {
				data.put("delProject", false);
			}

			data.put("series", new ArrayList<>(0));
			data.put("pushLog", false);
			String seriesId = MapUtil.getStr(bp, "series_id");
			data.put("seriesId", seriesId);
			if (doorInt > 0) {
				data.replace("pushLog", true);
			}

			if (StrUtil.isNotBlank(seriesId)) {
				List<String> seriesIds = new ArrayList<>(Arrays.asList(seriesId.split(",")));
				if (seriesIds.contains(udId)) {
					data.replace("pushLog", true);
				}
				List<UserDoorway> uds = userDoorwayService.list("id", SqlEmnus.IN, seriesIds, "name", "mobile");
				if (CollUtil.isNotEmpty(uds)) {
					List<Map<String, String>> series = uds.stream().map(ud -> {
						Map<String, String> m = new HashMap<>(2);
						m.put("name", ud.getName());
						m.put("mobile", ud.getMobile());
						return m;
					}).collect(Collectors.toList());

					data.replace("series", series);
				}
			}

			data.put("planDays", MapUtil.getInt(bp, "plan_days"));
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

		return collect;
	}

	/**
	 * @dec 获取门店概况
	 * @date Mar 28, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param json
	 * @param count
	 * @return
	 */
	@Override
	public R detailV2(String id, JSONObject json, int count) {
		Map<String, Object> result = new HashMap<>(4);
		QueryWrapper<UserDoorway> udc = new QueryWrapper<>();
		udc.eq("type", UserDoorway.TYPE_DOORWAY).and(i -> i.eq("doorway_id", id))
				.and(i -> i.eq("user_id", json.getStr("id")));
		int udCount = userDoorwayService.count(udc);
		if (count > 0 || udCount > 0) {
			result.put("edit", true);
		} else {
			result.put("edit", false);
		}

		if (udCount > 0) {
			result.put("imgEdit", true);
		} else {
			result.put("imgEdit", false);
		}

		Doorway doorway = getById(id);
		String[] dfls = { "orgId", "status", "createId", "createTime", "updateTime", "modelIds" };
		Map<String, Object> d = CommonUtil.beanToMapFl(doorway, dfls);

		DoorwayCategory category = doorwayCategoryService.getOne("id", SqlEmnus.EQ, doorway.getCategoryId(), "name",
				"val");
		d.put("categoryName", category.getName());

		QueryWrapper<DoorwayModel> dmqw = new QueryWrapper<>();
		dmqw.eq("doorway_id", id).and(i -> i.eq("category_id", doorway.getCategoryId())).select("model_id");
		List<String> modelIds = doorwayModelService.list(dmqw).stream().map(DoorwayModel::getModelId)
				.collect(Collectors.toList());
		List<Map<String, String>> models = modelService.list("id", SqlEmnus.IN, modelIds, "id", "name").stream()
				.map(m -> {
					Map<String, String> data = new HashMap<>(2);
					data.put("id", m.getId());
					data.put("name", m.getName());
					return data;
				}).collect(Collectors.toList());
		d.put("models", models);
		result.put("doorway", d);

		if (category.getVal() == DoorwayCategory.VAL_BUILD) {
			BuildEnginer buildEnginer = buildEnginerService.getOne("doorway_id", SqlEmnus.EQ, id);
			if (buildEnginer != null) {
				String[] fls = { "doorwayId", "actualDate", "status", "createTime", "updateTime", "seriesMobile",
						"seriesName", "projectName", "projectMobile", "delProjectMobile", "delSeriesMobile",
						"ownerName", "ownerMobile", "devName", "devMobile" };
				Map<String, Object> be = CommonUtil.beanToMapFl(buildEnginer, fls);
				be.replace("contractSignDate", buildEnginer.getContractSignDate().toString().replaceAll("-", "."));

				if (buildEnginer.getPrepareDate() != null) {
					be.replace("prepareDate", buildEnginer.getPrepareDate().toString().replaceAll("-", "."));
				}
				if (buildEnginer.getForecastDate() != null) {
					be.replace("forecastDate", buildEnginer.getForecastDate().toString().replaceAll("-", "."));
				}

				List<String> userDoorwayIds = userBuildEnginerService
						.list("build_enginer_id", SqlEmnus.EQ, buildEnginer.getId(), "user_doorway_id").stream()
						.map(UserBuildEnginer::getUserDoorwayId).collect(Collectors.toList());
				List<Map<String, String>> series = userDoorwayService
						.list("id", SqlEmnus.IN, userDoorwayIds, "name", "mobile").stream().map(ud -> {
							Map<String, String> data = new HashMap<>(2);
							data.put("name", ud.getName());
							data.put("mobile", ud.getMobile());
							return data;
						}).collect(Collectors.toList());
				be.put("series", series);

				String[] ownerNames = buildEnginer.getOwnerName().split(",");
				String[] ownerMobiles = buildEnginer.getOwnerMobile().split(",");
				List<Map<String, String>> owners = new ArrayList<>(ownerNames.length);
				for (int i = 0; i < ownerNames.length; i++) {
					Map<String, String> owner = new HashMap<>(2);
					owner.put("name", ownerNames[i]);
					owner.put("mobile", ownerMobiles[i]);
					owners.add(owner);
				}
				be.put("owners", owners);

				String[] devNames = buildEnginer.getDevName().split(",");
				String[] devMobiles = buildEnginer.getDevMobile().split(",");
				List<Map<String, String>> devs = new ArrayList<>(devNames.length);
				for (int i = 0; i < devNames.length; i++) {
					Map<String, String> dev = new HashMap<>(2);
					dev.put("name", devNames[i]);
					dev.put("mobile", devMobiles[i]);
					devs.add(dev);
				}
				be.put("devs", devs);

				if (StrUtil.isNotBlank(buildEnginer.getProjectName())) {
					String[] projectNames = buildEnginer.getProjectName().split(",");
					String[] projectMobiles = buildEnginer.getProjectMobile().split(",");
					List<Map<String, String>> projects = new ArrayList<>(projectNames.length);
					for (int i = 0; i < projectNames.length; i++) {
						Map<String, String> project = new HashMap<>(2);
						project.put("name", projectNames[i]);
						project.put("mobile", projectMobiles[i]);
						projects.add(project);
					}
					be.put("projects", projects);
				} else {
					be.put("projects", new ArrayList<>(0));
				}

				result.put("buildEnginer", be);
			}
		}

		return R.ok(result);
	}

	/**
	 * @dec 获取图片列表
	 * @date Mar 29, 2019
	 * @author gaochao
	 * @since 2.0.1
	 * @param id
	 * @param condition
	 * @param current
	 * @param size
	 * @return
	 */
	@Override
	public R fileImages(String id, String condition, Integer current, Integer size) {
		Map<String, Object> params = new HashMap<>(4);
		params.put("doorwayId", id);
		params.put("condition", condition);
		params.put("current", (current - 1) * size);
		params.put("size", size);

		List<Map<String, Object>> list = fileImageService.queryList(params);
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		return R.ok(list);
	}

}
