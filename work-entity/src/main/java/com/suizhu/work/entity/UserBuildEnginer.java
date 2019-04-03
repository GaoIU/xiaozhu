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
 * 用户表 - 筹建工程表 中间关联表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserBuildEnginer extends Model<UserBuildEnginer> {

	private static final long serialVersionUID = 1L;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 筹建工程ID
	 */
	private String buildEnginerId;

	/**
	 * 工程经理ID
	 */
	private String userDoorwayId;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public UserBuildEnginer(String buildEnginerId, String userDoorwayId) {
		super();
		this.buildEnginerId = buildEnginerId;
		this.userDoorwayId = userDoorwayId;
	}

}
