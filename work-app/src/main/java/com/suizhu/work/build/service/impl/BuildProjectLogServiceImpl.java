package com.suizhu.work.build.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.FdfsClient;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.common.util.SpringContextHolder;
import com.suizhu.work.build.mapper.BuildProjectLogMapper;
import com.suizhu.work.build.service.BuildProjectLogService;
import com.suizhu.work.build.service.BuildProjectService;
import com.suizhu.work.entity.BuildProject;
import com.suizhu.work.entity.BuildProjectLog;
import com.suizhu.work.entity.FileAudio;
import com.suizhu.work.entity.FileImage;
import com.suizhu.work.entity.FileVideo;
import com.suizhu.work.file.service.FileAudioService;
import com.suizhu.work.file.service.FileImageService;
import com.suizhu.work.file.service.FileVideoService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 工程项目记录表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@Service
@AllArgsConstructor
public class BuildProjectLogServiceImpl extends ServiceImpl<BuildProjectLogMapper, BuildProjectLog>
		implements BuildProjectLogService {

	private final FileImageService fileImageService;

	private final FileAudioService fileAudioService;

	private final FileVideoService fileVideoService;

	private final FdfsClient fdfsClient;

	/**
	 * @dec 新增项目日志
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param buildProjectLog
	 * @param doorwayId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean add(BuildProjectLog buildProjectLog, String doorwayId) {
		boolean save = save(buildProjectLog);
		if (save) {
			String audioFileId = buildProjectLog.getAudioFileId();
			if (StrUtil.isNotBlank(audioFileId)) {
				FileAudio fileAudio = fileAudioService.getOne("file_id", SqlEmnus.EQ, audioFileId);
				fileAudio.setBuildProjectLogId(buildProjectLog.getId());
				fileAudio.setStatus(FileAudio.STATUS_PERM);
				fileAudioService.updateById(fileAudio);
			}

			String imageFileIds = buildProjectLog.getImageFileIds();
			if (StrUtil.isNotBlank(imageFileIds)) {
				ArrayList<String> imageFileIdList = CollUtil.toList(imageFileIds.split(","));
				List<FileImage> iamges = fileImageService.list("file_id", SqlEmnus.IN, imageFileIdList);
				iamges.forEach(i -> {
					i.setBuildProjectLogId(buildProjectLog.getId());
					i.setStatus(FileImage.STATUS_PERM);
					i.setType(FileImage.TYPE_PROJECT);
					i.setDoorwayId(doorwayId);
				});
				fileImageService.updateBatchById(iamges);
			}

			String videoFileIds = buildProjectLog.getVideoFileIds();
			if (StrUtil.isNotBlank(videoFileIds)) {
				ArrayList<String> videoFileIdList = CollUtil.toList(videoFileIds.split(","));
				List<FileVideo> videos = fileVideoService.list("file_id", SqlEmnus.IN, videoFileIdList);
				videos.forEach(v -> {
					v.setBuildProjectLogId(buildProjectLog.getId());
					v.setStatus(FileVideo.STATUS_PERM);
				});
				fileVideoService.updateBatchById(videos);
			}
		}

		return save;
	}

	/**
	 * @dec 删除日志
	 * @date Mar 11, 2019
	 * @author gaochao
	 * @param id
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R del(String id) {
		BuildProjectService buildProjectService = SpringContextHolder.getBean("buildProjectServiceImpl",
				BuildProjectService.class);
		BuildProjectLog buildProjectLog = getById(id);
		boolean remove = removeById(id);
		if (remove) {
			IPage<BuildProjectLog> page = new Page<>(1, 1);
			String buildProjectId = buildProjectLog.getBuildProjectId();
			QueryWrapper<BuildProjectLog> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("build_project_id", buildProjectId);
			queryWrapper.orderByDesc("create_time");
			page = page(page, queryWrapper);
			BuildProject buildProject = buildProjectService.getById(buildProjectId);
			LocalDate now = LocalDate.now();
			LocalDate beginDate = buildProject.getBeginDate();
			LocalDate endDate = buildProject.getEndDate();

			if (CollUtil.isEmpty(page.getRecords())) {
				if (beginDate.isBefore(now) || beginDate.isEqual(now)) {
					buildProject.setStatus(BuildProject.STATUS_DOING);

					long abs = Math.abs(ChronoUnit.DAYS.between(beginDate, now));
					if (abs >= 1) {
						buildProject.setEarly(BuildProject.EARLY_WARNING);
					}
				} else {
					buildProject.setStatus(BuildProject.STATUS_NOT_START);
					buildProject.setEarly(BuildProject.EARLY_NORMAL);
				}
				if (endDate.isBefore(now)) {
					buildProject.setStatus(BuildProject.STATUS_DOING);
					buildProject.setEarly(BuildProject.EARLY_OVERTIME_DOING);
				}
			} else {
				if (buildProjectLog.getOverdueDays() != null) {
					Integer overdueDays = buildProject.getOverdueDays();
					overdueDays -= buildProjectLog.getOverdueDays();
					if (overdueDays < 0) {
						overdueDays = 0;
					}
					buildProject.setOverdueDays(overdueDays);
				}

				BuildProjectLog projectLog = page.getRecords().get(0);
				Integer early = projectLog.getEarly();
				if (BuildProjectLog.EARLY_NORMAL_DOING == early) {
					buildProject.setEarly(BuildProject.EARLY_NORMAL);
					buildProject.setStatus(BuildProject.STATUS_DOING);
				} else if (BuildProjectLog.EARLY_OVERTIME_DOING == early) {
					buildProject.setEarly(BuildProject.EARLY_OVERTIME_DOING);
				} else if (BuildProjectLog.EARLY_WARNING == early) {
					buildProject.setEarly(BuildProject.EARLY_WARNING);
					buildProject.setStatus(BuildProject.STATUS_DOING);
				}
			}
			if (buildProject.getStatus() == BuildProject.STATUS_DOING) {
				buildProject.setActualDate(null);
			}
			buildProjectService.updateById(buildProject);

			List<FileImage> images = fileImageService.list("build_project_log_id", SqlEmnus.EQ, id, "file_id");
			List<String> fileIds = new ArrayList<>();
			if (CollUtil.isNotEmpty(images)) {
				fileImageService.remove("build_project_log_id", SqlEmnus.EQ, id);
				List<String> imgs = images.stream().map(FileImage::getFileId).collect(Collectors.toList());
				fileIds.addAll(imgs);
			}

			List<FileAudio> audios = fileAudioService.list("build_project_log_id", SqlEmnus.EQ, id, "file_id");
			if (CollUtil.isNotEmpty(audios)) {
				fileAudioService.remove("build_project_log_id", SqlEmnus.EQ, id);
				List<String> auds = audios.stream().map(FileAudio::getFileId).collect(Collectors.toList());
				fileIds.addAll(auds);
			}

			List<FileVideo> videos = fileVideoService.list("build_project_log_id", SqlEmnus.EQ, id, "file_id");
			if (CollUtil.isNotEmpty(videos)) {
				fileVideoService.remove("build_project_log_id", SqlEmnus.EQ, id);
				List<String> vies = videos.stream().map(FileVideo::getFileId).collect(Collectors.toList());
				fileIds.addAll(vies);
			}

			if (CollUtil.isNotEmpty(fileIds)) {
				fdfsClient.delete(fileIds);
			}

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 获取周报日期
	 * @date Mar 12, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> weekTime(Map<String, Object> params) {
		return baseMapper.weekTime(params);
	}

	/**
	 * @dec 获取月份选项卡
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @return
	 */
	@Override
	public List<Integer> weekMonth() {
		return baseMapper.weekMonth();
	}

}
