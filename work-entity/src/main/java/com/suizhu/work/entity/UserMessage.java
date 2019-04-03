package com.suizhu.work.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息推送表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserMessage extends Model<UserMessage> {

	private static final long serialVersionUID = 1L;

	/** 类型：0-开工 */
	public static final int TYPE_START = 0;

	/** 类型：1-未确认进度 */
	public static final int TYPE_NOT_FLUSH = 1;

	/** 类型：2-预警 */
	public static final int TYPE_EARLY = 2;

	/** 类型：3-超时 */
	public static final int TYPE_OVERTIME = 3;

	/** 是否读取：0-否 */
	public static final int REMIND_NOT = 0;

	/** 是否读取：1-是 */
	public static final int REMIND_YES = 1;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 抬头名称
	 */
	private String name;

	/**
	 * 提示消息
	 */
	private String message;

	/**
	 * 是否读取：0-否，1-是
	 */
	private Integer remind = REMIND_NOT;

	/**
	 * 类型：0-开工，1-未确认进度，2-预警，3-超时
	 */
	private Integer type;

	/**
	 * 门店ID
	 */
	private String doorwayId;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public UserMessage(String name, String message, Integer type, String doorwayId, String userId) {
		super();
		this.name = name;
		this.message = message;
		this.type = type;
		this.doorwayId = doorwayId;
		this.userId = userId;
	}

}
