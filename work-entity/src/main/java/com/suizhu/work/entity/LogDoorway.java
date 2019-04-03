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
 * 门店日志
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LogDoorway extends Model<LogDoorway> {

	private static final long serialVersionUID = 1L;

	/** 日志类型：0-修改门店 */
	public static final int TYPE_EDIT = 0;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 门店ID
	 */
	private String doorwayId;

	/**
	 * 日志类型：0-修改门店
	 */
	private Integer type;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public LogDoorway(String userId, String doorwayId, Integer type) {
		super();
		this.userId = userId;
		this.doorwayId = doorwayId;
		this.type = type;
	}

}
