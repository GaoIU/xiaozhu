package com.suizhu.work.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 筹建工程表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuildEnginer extends Model<BuildEnginer> {

	private static final long serialVersionUID = 1L;

	/** 工程状态：0-未开始 */
	public static final int STATUS_NOT_START = 0;

	/** 工程状态：1-进行中 */
	public static final int STATUS_DOING = 1;

	/** 工程状态：2-已完成 */
	public static final int STATUS_FLUSH = 2;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 年属项目
	 */
	@NotBlank(message = "年属项目不能为空！")
	@Length(max = 32, message = "年属项目长度不能超过32位！")
	private String name;

	/**
	 * 品牌
	 */
	@NotBlank(message = "品牌不能为空！")
	@Length(max = 32, message = "品牌长度不能超过32位！")
	private String brand;

	/**
	 * 设计标注
	 */
	@NotBlank(message = "设计标注不能为空！")
	@Length(max = 8, message = "设计标注长度不能超过8位！")
	private String design;

	/**
	 * 项目分类
	 */
	private String type;

	/**
	 * 是否统采：0-是，1-否
	 */
	private Integer purchase;

	/**
	 * 所属区域
	 */
	@NotBlank(message = "所属区域不能为空！")
	@Length(max = 32, message = "所属区域长度不能超过32位！")
	private String region;

	/**
	 * 项目编码
	 */
	@NotBlank(message = "项目编码不能为空！")
	@Length(max = 64, message = "项目编码长度不能超过64位！")
	private String code;

	/**
	 * 门店ID
	 */
	@NotBlank(message = "门店ID不能为空！")
	private String doorwayId;

	/**
	 * 工程经理名称
	 */
	@NotBlank(message = "工程经理名称不能为空！")
	@TableField(exist = false)
	private String seriesName;

	/**
	 * 工程经理手机号码
	 */
	@NotBlank(message = "工程经理手机号码不能为空！")
	@TableField(exist = false)
	private String seriesMobile;

	/**
	 * 要删除的工程经理手机号码
	 */
	@TableField(exist = false)
	private String delSeriesMobile;

	/**
	 * 项目经理名称
	 */
	private String projectName;

	/**
	 * 项目经理手机号码
	 */
	private String projectMobile;

	/**
	 * 要删除的项目经理手机号码
	 */
	@TableField(exist = false)
	private String delProjectMobile;

	/**
	 * 业主代表名称
	 */
	@NotBlank(message = "业主代表名称不能为空！")
	private String ownerName;

	/**
	 * 业主代表手机号码
	 */
	@NotBlank(message = "业主代表手机号码不能为空！")
	private String ownerMobile;

	/**
	 * 开发经理名称
	 */
	@NotBlank(message = "开发经理名称不能为空！")
	private String devName;

	/**
	 * 开发经理手机号码
	 */
	@NotBlank(message = "开发经理手机号码不能为空！")
	private String devMobile;

	/**
	 * 合同签订日期
	 */
	@NotNull(message = "合同签订日期不能为空！")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate contractSignDate;

	/**
	 * 筹建开始日期
	 */
	@NotNull(message = "筹建开始日期不能为空！")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate prepareDate;

	/**
	 * 预计完工时间
	 */
	@NotNull(message = "预计完工日期不能为空！")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate forecastDate;

	/**
	 * 实际完成时间
	 */
	private LocalDate actualDate;

	/**
	 * 工程状态：0-未开始，1-进行中，2-已完成
	 */
	private Integer status;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	@TableField(fill = FieldFill.UPDATE)
	private LocalDateTime updateTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
