package com.suizhu.cms.entity;

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
 * 后台角色表
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysRole extends Model<SysRole> {

	private static final long serialVersionUID = 1L;

	/** 后台角色状态：0-正常 */
	public static final int STATUS_NORMAL = 0;

	/** 后台角色状态：1-禁用 */
	public static final int STATUS_DISABLE = 1;

	/** 超级管理员编码 */
	public static final String ADMIN_CODE = "ADMINISTRATOR";

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 角色名称
	 */
	@NotBlank(message = "角色名称不能为空！")
	@Length(max = 16, message = "角色名称长度不能超过16位！")
	private String name;

	/**
	 * 角色编码
	 */
	@NotBlank(message = "角色编码不能为空！")
	@Length(max = 32, message = "角色编码长度不能超过32位！")
	private String code;

	/**
	 * 角色状态：0-正常，1-禁用
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

	/**
	 * 后台资源ID
	 */
	@TableField(exist = false)
	private String sysResourceIds;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
