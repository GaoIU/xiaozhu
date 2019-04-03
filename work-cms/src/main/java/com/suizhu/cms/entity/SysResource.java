package com.suizhu.cms.entity;

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
 * 后台资源表
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysResource extends Model<SysResource> {

	private static final long serialVersionUID = 1L;

	/** 后台资源状态：0-正常 */
	public static final int STATUS_NORMAL = 0;

	/** 后台资源状态：1-禁用 */
	public static final int STATUS_DISABLE = 1;

	/** 后台资源类型：0-菜单 */
	public static final int TYPE_MENU = 0;

	/** 后台资源类型：1-按钮 */
	public static final int TYPE_BUTTON = 1;

	/** 后台资源类型：2-功能 */
	public static final int TYPE_FUNCTION = 2;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 资源名称
	 */
	@NotBlank(message = "资源名称不能为空！")
	@Length(max = 16, message = "资源名称长度不能超过16位！")
	private String name;

	/**
	 * 资源编码
	 */
	@NotBlank(message = "资源编码不能为空！")
	@Length(max = 32, message = "资源编码长度不能超过32位！")
	private String code;

	/**
	 * 资源地址
	 */
	@Length(max = 255, message = "资源地址长度不能超过255位！")
	private String url;

	/**
	 * 访问方式
	 */
	@NotBlank(message = "请选择访问方式！")
	@Length(max = 16, message = "访问方式长度不能超过16位！")
	private String method;

	/**
	 * 资源类型：0-菜单，1-按钮，2-功能
	 */
	@NotNull(message = "请选择资源类型！")
	private Integer type;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 图标
	 */
	private String icon;

	/**
	 * 上级资源ID
	 */
	private String parentId;

	/**
	 * 资源描述
	 */
	@Length(max = 200, message = "资源描述长度不能超过200位！")
	private String description;

	/**
	 * 资源状态：0-正常，1-禁用
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
