package com.suizhu.cms.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 后台用户表
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser extends Model<SysUser> {

	private static final long serialVersionUID = 1L;

	/** 后台用户状态：0-正常 */
	public static final int STATUS_NORMAL = 0;

	/** 后台用户状态：1-禁用 */
	public static final int STATUS_DISABLE = 1;

	/** 后台用户默认密码 */
	public static final String DEFAULT_PASSWORD = "123456";

	/** 后台用户默认session存储主键 */
	public static final String DEFAULT_SESSION_KEY = "SYS_USER_SESSION_KEY";

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 昵称
	 */
	@NotBlank(message = "昵称不能为空！")
	@Length(max = 8, message = "昵称长度不能超过8位！")
	private String nickName;

	/**
	 * 真实姓名
	 */
	@Length(max = 8, message = "真实姓名长度不能超过8位！")
	private String realName;

	/**
	 * 账号
	 */
	@NotBlank(message = "账号不能为空！")
	@Length(max = 8, message = "账号长度不能超过8位！")
	private String username;

	/**
	 * 密码
	 */
	@JsonIgnore
	private String password;

	/**
	 * 手机号码
	 */
	@NotBlank(message = "手机号码不能为空！")
	@Pattern(regexp = "0?(13|14|15|17|18|19)[0-9]{9}", message = "手机号码不合法！")
	private String mobile;

	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 后台用户状态：0-正常，1-禁用
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
	 * 后台角色ID
	 */
	@TableField(exist = false)
	private String sysRoleIds;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
