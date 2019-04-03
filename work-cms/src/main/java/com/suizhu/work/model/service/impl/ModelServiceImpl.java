package com.suizhu.work.model.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.FdfsClient;
import com.suizhu.common.core.R;
import com.suizhu.common.core.service.impl.ServiceImpl;
import com.suizhu.work.doorway.service.DoorwayCategoryService;
import com.suizhu.work.entity.DoorwayCategory;
import com.suizhu.work.entity.Model;
import com.suizhu.work.entity.ModelBuild;
import com.suizhu.work.model.mapper.ModelMapper;
import com.suizhu.work.model.service.ModelBuildService;
import com.suizhu.work.model.service.ModelService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 模版表 服务实现类
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
@Service
@AllArgsConstructor
public class ModelServiceImpl extends ServiceImpl<ModelMapper, Model> implements ModelService {

	private final DoorwayCategoryService doorwayCategoryService;

	private final FdfsClient fdfsClient;

	private final ModelBuildService modelBuildService;

	/**
	 * @dec 获取模版列表
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param params
	 * @return
	 */
	@Override
	public R queryList(Map<String, Object> params) {
		Integer current = MapUtils.getInteger(params, "current", 1);
		Integer size = MapUtils.getInteger(params, "size", 15);
		String name = MapUtils.getString(params, "name");
		String beginTime = MapUtils.getString(params, "beginTime");
		String endTime = MapUtils.getString(params, "endTime");
		Integer status = MapUtils.getInteger(params, "status");
		QueryWrapper<Model> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByDesc("create_time");

		if (StrUtil.isNotBlank(name)) {
			queryWrapper.and(i -> i.like("name", name));
		}
		if (StrUtil.isNotBlank(beginTime)) {
			queryWrapper.and(i -> i.ge("expire_time", beginTime));
		}
		if (StrUtil.isNotBlank(endTime)) {
			queryWrapper.and(i -> i.le("create_time", endTime + " 23:59:59"));
		}
		if (status != null) {
			queryWrapper.and(i -> i.eq("status", status));
		}

		IPage<Model> page = new Page<>(current, size);
		page = page(page, queryWrapper);

		IPage<Map<String, Object>> data = new Page<>(current, size);
		data.setTotal(page.getTotal());

		List<Model> records = page.getRecords();
		if (CollUtil.isNotEmpty(records)) {
			List<Map<String, Object>> collect = records.stream().map(m -> {
				Map<String, Object> r = new HashMap<>(6);
				r.put("id", m.getId());
				r.put("name", m.getName());
				DoorwayCategory category = doorwayCategoryService.getOne("id", SqlEmnus.EQ, m.getCategoryId(), "name");
				r.put("categoryName", category.getName());
				r.put("fileId", m.getFileId());
				r.put("status", m.getStatus());
				r.put("createTime", m.getCreateTime());
				return r;
			}).collect(Collectors.toList());
			data.setRecords(collect);
		} else {
			data.setRecords(new ArrayList<>(0));
		}
		return R.ok(data);
	}

	/**
	 * @dec 新增模版
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param model
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R addModel(@Valid Model model, MultipartFile file) throws IOException {
		String fileId = fdfsClient.upload(file);
		model.setFileId(fileId);
		boolean save = save(model);
		if (save) {
			List<ModelBuild> modelBuilds = getModelBuilds(model.getId(), file.getBytes());
			modelBuildService.saveBatch(modelBuilds);

			return R.ok();
		}

		return R.error();
	}

	/**
	 * @dec 获取模版明细
	 * @date Mar 5, 2019
	 * @author gaochao
	 * @param modelId
	 * @param bs
	 * @return
	 * @throws IOException
	 */
	private List<ModelBuild> getModelBuilds(String modelId, byte[] bs) throws IOException {
		Workbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new ByteArrayInputStream(bs));
		} catch (Exception e) {
			workbook = new XSSFWorkbook(new ByteArrayInputStream(bs));
		}

		List<ModelBuild> modelBuilds = new ArrayList<>();
		// 有多少个sheet
		int sheets = workbook.getNumberOfSheets();
		for (int i = 0; i < sheets; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			// 获取多少行
			int rows = sheet.getPhysicalNumberOfRows();
			for (int j = 1; j < rows; j++) {
				Row row = sheet.getRow(j);
				row.getCell(0).setCellType(CellType.STRING);
				String sort = row.getCell(0).getStringCellValue();
				String name = row.getCell(1).getStringCellValue();
				Integer beginDay = (int) row.getCell(2).getNumericCellValue();
				Integer day = (int) row.getCell(3).getNumericCellValue();

				ModelBuild modelBuild = new ModelBuild(name, day, Integer.valueOf(sort), beginDay, modelId);
				modelBuilds.add(modelBuild);
			}
		}

		return modelBuilds;
	}

	/**
	 * @dec 修改模版
	 * @date Mar 18, 2019
	 * @author gaochao
	 * @param model
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public R editModel(Model model, MultipartFile file) throws IOException {
		if (file != null && !file.isEmpty()) {
			String fileId = fdfsClient.updateFile(file, model.getFileId());
			model.setFileId(fileId);

			modelBuildService.remove("model_id", SqlEmnus.EQ, model.getId());
			List<ModelBuild> modelBuilds = getModelBuilds(model.getId(), file.getBytes());
			modelBuildService.saveBatch(modelBuilds);
		}

		boolean update = updateById(model);
		if (update) {
			return R.ok();
		}

		return R.error();
	}

}
