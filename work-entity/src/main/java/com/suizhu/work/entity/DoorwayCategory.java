package com.suizhu.work.entity;

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
 * 门店分类表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DoorwayCategory extends Model<DoorwayCategory> {

	private static final long serialVersionUID = 1L;

	/** 分类状态：0-正常 */
	public static final int STATUS_NORMAL = 0;

	/** 分类状态：1-禁用 */
	public static final int STATUS_DISABLE = 1;
	
	/** 代表值代表：0-筹建店 */
	public static final int VAL_BUILD = 0;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 分类名称
	 */
	@NotBlank(message = "分类名称不能为空！")
	@Length(max = 16, message = "分类名称长度不能超过16位长度！")
	private String name;

	/**
	 * 分类代表值
	 */
	@NotNull(message = "分类代表值不能为空！")
	private Integer val;

	/**
	 * 分类状态：0-正常，1-禁用
	 */
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
