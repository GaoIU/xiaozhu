package com.suizhu.work.mansion.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.entity.Floor;
import com.suizhu.work.entity.Room;
import com.suizhu.work.mansion.mapper.FloorMapper;
import com.suizhu.work.mansion.service.FloorService;
import com.suizhu.work.mansion.service.RoomService;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 楼层表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@Service
@AllArgsConstructor
public class FloorServiceImpl extends ServiceImpl<FloorMapper, Floor> implements FloorService {

	private final RoomService roomService;

	/**
	 * @dec 删除楼层
	 * @date Mar 8, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R del(String id) {
		boolean remove = removeById(id);
		if (remove) {
			List<Room> rooms = roomService.list("floor_id", SqlEmnus.EQ, id, "id");
			if (CollUtil.isNotEmpty(rooms)) {
				List<String> roomIds = rooms.stream().map(Room::getId).collect(Collectors.toList());
				roomService.removeByIds(roomIds);
			}

			return R.ok();
		}

		return R.error();
	}

}
