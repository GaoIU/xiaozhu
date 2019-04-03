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
 * 筹建工程日志
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LogBuildEnginer extends Model<LogBuildEnginer> {

	private static final long serialVersionUID = 1L;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 筹建工程ID
	 */
	private String buildEnginerId;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public LogBuildEnginer(String userId, String buildEnginerId) {
		super();
		this.userId = userId;
		this.buildEnginerId = buildEnginerId;
	}

}
