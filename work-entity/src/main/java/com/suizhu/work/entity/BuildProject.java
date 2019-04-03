package com.suizhu.work.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 筹建项目表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuildProject extends Model<BuildProject> {

	private static final long serialVersionUID = 1L;

	/** 预警：0-正常 */
	public static final int EARLY_NORMAL = 0;

	/** 预警：1-预警 */
	public static final int EARLY_WARNING = 1;

	/** 预警：2-超时进行 */
	public static final int EARLY_OVERTIME_DOING = 2;

	/** 预警：3-超时完成 */
	public static final int EARLY_OVERTIME_FLUSH = 3;

	/** 筹建项目类型：0-筹建工程计划内 */
	public static final int TYPE_IN = 0;

	/** 筹建项目类型：1-筹建工程计划外 */
	public static final int TYPE_OUT = 1;

	/** 项目状态：0-未开始 */
	public static final int STATUS_NOT_START = 0;

	/** 项目状态：1-进行中 */
	public static final int STATUS_DOING = 1;

	/** 项目状态：2-已完成 */
	public static final int STATUS_FLUSH = 2;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 筹建项目名称
	 */
	@NotBlank(message = "工序名称不能为空！")
	@Length(max = 64, message = "工序名称长度不能超过64位！")
	private String name;

	/**
	 * 筹建项目组长ID
	 */
	private String seriesId;

	/**
	 * 筹建工序组长名称
	 */
	// @NotBlank(message = "工序组长名称不能为空！")
	@Length(max = 12, message = "工序组长名称长度不能超过12位！")
	@TableField(exist = false)
	private String seriesName;

	/**
	 * 筹建工序组长手机号码
	 */
	// @NotBlank(message = "工序组长手机号码不能为空！")
	// @Pattern(regexp = "0?(13|14|15|17|18|19)[0-9]{9}", message = "工序组长手机号码不合法！")
	@TableField(exist = false)
	private String seriesMobile;

	/**
	 * 计划开始时间
	 */
	@NotNull(message = "计划开始时间不能为空！")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate beginDate;

	/**
	 * 计划结束时间
	 */
	@NotNull(message = "计划结束时间不能为空！")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	/**
	 * 计划天数
	 */
	@NotNull(message = "计划天数不能为空！")
	private Integer planDays;

	/**
	 * 逾期天数
	 */
	private Integer overdueDays = 0;

	/**
	 * 实际完成时间
	 */
	@TableField(strategy = FieldStrategy.IGNORED)
	private LocalDate actualDate;

	/**
	 * 筹建项目类型：0-筹建工程计划内，1-筹建工程计划外
	 */
	private Integer type = TYPE_OUT;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 预警：0-正常，1-预警，2-超时进行，3-超时完成
	 */
	private Integer early = EARLY_NORMAL;

	/**
	 * 筹建项目状态：0-未开始，1-进行中，2-已完成
	 */
	private Integer status = STATUS_NOT_START;

	/**
	 * 上级ID
	 */
	private String parentId;

	/**
	 * 筹建工程ID
	 */
	@NotBlank(message = "筹建工程ID不能为空！")
	private String buildEnginerId;

	/**
	 * 房间ID
	 */
	private String roomId;

	/**
	 * 创建人ID
	 */
	private String createId;

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
