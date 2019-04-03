package com.suizhu.work.mansion.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.work.build.service.BuildProjectLogService;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildProjectLog;
import com.suizhu.work.entity.Floor;
import com.suizhu.work.entity.Mansion;
import com.suizhu.work.entity.Room;
import com.suizhu.work.entity.UserDoorway;
import com.suizhu.work.mansion.service.FloorService;
import com.suizhu.work.mansion.service.MansionService;
import com.suizhu.work.mansion.service.RoomService;
import com.suizhu.work.user.service.UserDoorwayService;
import com.suizhu.work.util.CommonUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 房间表 前端控制器
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@RestController
@AllArgsConstructor
@RequestMapping("room")
public class RoomController {

	private final RoomService roomService;

	private final BuildProjectService buildProjectService;

	private final BuildProjectLogService buildProjectLogService;

	private final UserDoorwayService userDoorwayService;

	private final FloorService floorService;

	private final MansionService mansionService;

	/**
	 * @dec 新增房间
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param room
	 * @param token
	 * @return
	 */
	@PostMapping
	public R save(@Valid Room room, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
		JSONObject json = CommonUtil.decodeToken(token);
		String[] names = room.getName().split(",");
		List<Room> rooms = new ArrayList<>(names.length);
		for (String name : names) {
			if (name.trim().length() <= 0 || name.trim().length() > 64) {
				throw new BadRequestException("房间名称长度不能超过64位！");
			}
			Room r = new Room(name, room.getFloorId(), json.getStr("id"));
			rooms.add(r);
		}
		boolean save = roomService.saveBatch(rooms);
		if (save) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 修改房间
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param room
	 * @param token
	 * @return
	 */
	@PutMapping
	public R edit(@Valid Room room) {
		boolean update = roomService.updateById(room);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 删除房间
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @param token
	 * @return
	 */
	@DeleteMapping("/{id}")
	public R del(@PathVariable String id) {
		boolean remove = roomService.removeById(id);
		if (remove) {
			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 获取房间信息
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @param id
	 * @param status
	 * @return
	 */
	@GetMapping("/{id}")
	public R info(@PathVariable String id, Integer status) {
		QueryWrapper<BuildProject> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("room_id", id);
		if (status != null) {
			queryWrapper.and(i -> i.eq("status", status));
		}
		List<BuildProject> list = buildProjectService.list(queryWrapper);
		if (CollUtil.isEmpty(list)) {
			return R.ok(new ArrayList<>(0));
		}

		LocalDate now = LocalDate.now();
		List<Map<String, Object>> collect = list.stream().map(bp -> {
			Map<String, Object> data = new HashMap<>(11);
			data.put("id", bp.getId());
			data.put("sort", bp.getSort());
			data.put("name", bp.getName());

			LocalDate beginDate = bp.getBeginDate();
			if (now.getYear() == beginDate.getYear()) {
				data.put("beginDate", beginDate.format(DateTimeFormatter.ofPattern("MM.dd")));
			} else {
				data.put("beginDate", beginDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
			}
			LocalDate endDate = bp.getEndDate();
			if (now.getYear() == endDate.getYear()) {
				data.put("endDate", endDate.format(DateTimeFormatter.ofPattern("MM.dd")));
			} else {
				data.put("endDate", endDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
			}
			data.put("planDays", bp.getPlanDays() + bp.getOverdueDays());

			QueryWrapper<BuildProjectLog> condition = new QueryWrapper<>();
			condition.eq("build_project_id", bp.getId());
			condition.and(i -> i.lt("create_time", now.plusDays(1).toString() + " 00:00:00"));
			int count = buildProjectLogService.count(condition);
			long actualDay;
			LocalDate actualDate = bp.getActualDate();

			if (count <= 0) {
				actualDay = 0;
			} else {
				if (actualDate == null && BuildProject.STATUS_DOING == bp.getStatus()) {
					if (beginDate.isBefore(now) || beginDate.isEqual(now)) {
						actualDay = Math.abs(ChronoUnit.DAYS.between(beginDate, now)) + 1;
					} else {
						actualDay = -1;
					}
				} else if (actualDate == null) {
					actualDay = ChronoUnit.DAYS.between(beginDate, now);
				} else {
					if (actualDate.isBefore(endDate) || actualDate.isEqual(endDate)) {
						actualDay = ChronoUnit.DAYS.between(beginDate, endDate) + 1;
					} else {
						actualDay = ChronoUnit.DAYS.between(beginDate, actualDate) + 1;
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
			if (StrUtil.isNotBlank(bp.getSeriesId())) {
				UserDoorway userDoorway = userDoorwayService.getOne("id", SqlEmnus.EQ, bp.getSeriesId(), "name",
						"mobile");
				data.put("seriesName", userDoorway.getName());
				data.put("seriesMobile", userDoorway.getMobile());
			}
			data.put("type", bp.getType());
			data.put("early", bp.getEarly());
			Room room = roomService.getById(id);
			Floor floor = floorService.getById(room.getFloorId());
			Mansion mansion = mansionService.getById(floor.getMansionId());
			data.replace("address", mansion.getName() + floor.getName() + room.getName());
			return data;
		}).collect(Collectors.toList());

		return R.ok(collect);
	}

}
