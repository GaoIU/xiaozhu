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
 * 大楼表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Mansion extends Model<Mansion> {

	private static final long serialVersionUID = 1L;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 大楼名称
	 */
	@NotBlank(message = "大楼名称不能为空！")
	@Length(max = 64, message = "大楼名称长度不能超过64位！")
	private String name;

	/**
	 * 门店ID
	 */
	@NotBlank(message = "门店ID不能为空！")
	private String doorwayId;

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
