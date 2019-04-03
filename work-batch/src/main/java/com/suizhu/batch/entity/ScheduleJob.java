package com.suizhu.batch.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 定时任务表
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ScheduleJob extends Model<ScheduleJob> {

	private static final long serialVersionUID = 1L;

	/** 是否并发执行：0-是 */
	public static final int CONCURRENT_IS = 0;

	/** 是否并发执行：1-否 */
	public static final int CONCURRENT_NOT = 1;

	/** 任务状态：0-已开启 */
	public static final int STATUS_OPEN = 0;

	/** 任务状态：1-已关闭 */
	public static final int STATUS_CLOSE = 1;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 任务名
	 */
	@NotBlank(message = "任务名不能为空！")
	@Length(max = 32, message = "任务名长度不能超过32位！")
	private String jobName;

	/**
	 * 任务组
	 */
	@NotBlank(message = "任务组不能为空！")
	@Length(max = 32, message = "任务组长度不能超过32位！")
	private String jobGroup;

	/**
	 * 时间表达式
	 */
	@NotBlank(message = "时间表达式不能为空！")
	@Length(max = 64, message = "时间表达式长度不能超过64位！")
	private String cron;

	/**
	 * 是否并发
	 */
	@NotNull(message = "请选择是否并发！")
	private Integer concurrent;

	/**
	 * 备注描述
	 */
	@Length(max = 250, message = "备注描述长度不能超过250位！")
	private String remark;

	/**
	 * 任务状态：0-开启，1-关闭
	 */
	@NotNull(message = "请选择任务状态！")
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
