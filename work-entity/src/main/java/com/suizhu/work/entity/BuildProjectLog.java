package com.suizhu.work.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工程项目记录表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuildProjectLog extends Model<BuildProjectLog> {

	private static final long serialVersionUID = 1L;

	/** 项目状态：0-正常完成 */
	public static final int STATUS_NORMAL = 0;

	/** 项目状态：1-提前完成 */
	public static final int STATUS_FLUSH = 1;

	/** 项目状态：2-预计超时 */
	public static final int STATUS_OVERTIME = 2;

	/** 预警：0-正常完成 */
	public static final int EARLY_NORMAL = 0;

	/** 预警：5-正常进行中 */
	public static final int EARLY_NORMAL_DOING = 5;

	/** 预警：1-提前完成 */
	public static final int EARLY_NORMAL_FLUSH = 1;

	/** 预警：2-预警 */
	public static final int EARLY_WARNING = 2;

	/** 预警：3-超时进行中 */
	public static final int EARLY_OVERTIME_DOING = 3;

	/** 预警：3-超时已完成 */
	public static final int EARLY_OVERTIME_FLUSH = 4;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 项目工程ID
	 */
	@NotBlank(message = "工序ID不能为空！")
	private String buildProjectId;

	/**
	 * 房间ID
	 */
	private String roomId;

	/**
	 * 描述
	 */
	@Length(max = 200, message = "描述长度不能超过200位！")
	private String remark;

	/**
	 * 预警：0-正常完成，1-提前完成，2-预警，3-超时进行中，4-超时已完成，5-正常进行中
	 */
	private Integer early;

	/**
	 * 逾期天数
	 */
	private Integer overdueDays = 0;

	/**
	 * 状态：0-正常完成，1-提前完成，2-预计超时
	 */
	private Integer status;

	/**
	 * 图片文件ID
	 */
	@TableField(exist = false)
	private String imageFileIds;

	/**
	 * 音频文件ID
	 */
	@TableField(exist = false)
	private String audioFileId;

	/**
	 * 视频文件ID
	 */
	@TableField(exist = false)
	private String videoFileIds;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
