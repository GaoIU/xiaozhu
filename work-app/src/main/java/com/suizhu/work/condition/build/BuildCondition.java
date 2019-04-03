package com.suizhu.work.condition.build;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 
 * @author gaochao
 * @date Mar 26, 2019
 */
@Data
public class BuildCondition {

	/** 查询类型：1-周 */
	public static final int TYPE_WEEK = 1;

	/** 查询类型：2-月 */
	public static final int TYPE_MONTH = 2;

	/**
	 * 查询类型：1-周，2-月，other-全部
	 */
	private Integer type = TYPE_WEEK;

	/**
	 * 工序状态
	 */
	private Integer status;

	/**
	 * 年
	 */
	private Integer year = LocalDate.now().getYear();

	/**
	 * 月
	 */
	private Integer month = LocalDate.now().getMonthValue();

	/**
	 * 周
	 */
	private String week;

	/**
	 * 日
	 */
	private Integer day;

	/**
	 * 门店ID
	 */
	@NotBlank(message = "门店ID不能为空！")
	private String id;

	/**
	 * 分类ID
	 */
	@NotBlank(message = "分类ID不能为空！")
	private String categoryId;

}
