package com.suizhu.work.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 模版表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Model extends com.baomidou.mybatisplus.extension.activerecord.Model<Model> {

	private static final long serialVersionUID = 1L;

	/** 模版状态：0-正常 */
	public static final int STATUS_NORMAL = 0;

	/** 模版状态：1-禁用 */
	public static final int STATUS_DISABLE = 1;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 模板名称
	 */
	@NotBlank(message = "模版名称不能为空！")
	@Length(max = 16, message = "模版名成长度不能超过16位！")
	private String name;

	/**
	 * 模板文件ID
	 */
	private String fileId;

	/**
	 * 门店分类ID
	 */
	@NotBlank(message = "请选择门店分类！")
	private String categoryId;

	/**
	 * 模版状态：0-正常，1-禁用
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
