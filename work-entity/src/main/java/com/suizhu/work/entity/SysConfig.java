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
 * 系统配置表
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysConfig extends Model<SysConfig> {

	private static final long serialVersionUID = 1L;

	/** 配置参数状态：0-正常 */
	public static final int STATUS_NORMAL = 0;

	/** 配置参数状态：1-禁用 */
	public static final int STATUS_DISABLE = 1;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 配置key
	 */
	@NotBlank(message = "配置key不能为空！")
	@Length(max = 32, message = "配置key长度不能超过32位！")
	private String name;

	/**
	 * 配置val
	 */
	@NotBlank(message = "配置val不能为空！")
	private String val;

	/**
	 * 描述
	 */
	@Length(max = 200, message = "描述长度不能超过200位！")
	private String description;

	/**
	 * 配置状态：0-正常，1-禁用
	 */
	private Integer status = STATUS_NORMAL;

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
