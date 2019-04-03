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
 * 公司人员操作日志表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-13
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LogUserOrg extends Model<LogUserOrg> {

	private static final long serialVersionUID = 1L;

	/** 操作类型：1-新增 */
	public static final int TYPE_ADD = 1;

	/** 操作类型：2-修改 */
	public static final int TYPE_EDIT = 2;

	/** 操作类型：3-删除 */
	public static final int TYPE_DEL = 3;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 操作类型：1-新增，2-修改，3-删除
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

	public LogUserOrg(String userId, Integer type) {
		super();
		this.userId = userId;
		this.type = type;
	}

}
