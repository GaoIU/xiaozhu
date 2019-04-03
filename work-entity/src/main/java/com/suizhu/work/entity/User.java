package com.suizhu.work.entity;

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
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-13
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User extends Model<User> {

	private static final long serialVersionUID = 1L;

	/** 用户状态：0-正常 */
	public static final int STATUS_NORMAL = 0;

	/** 用户状态：1-禁用 */
	public static final int STATUS_DISABLE = 1;

	/** 用户默认密码 */
	public static final String DEFAULT_PASSWORD = "123456";

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 用户账号
	 */
	@NotBlank(message = "账号不能为空！")
	@Pattern(regexp = "0?(13|14|15|17|18|19)[0-9]{9}", message = "账号不合法！")
	private String username;

	/**
	 * 账号密码
	 */
	@JsonIgnore
	private String password;

	/**
	 * 真实姓名
	 */
	@NotBlank(message = "姓名不能为空！")
	@Length(max = 12, message = "姓名长度不能超过12位！")
	private String realName;

	/**
	 * 用户头像
	 */
	private String avatar;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 邮箱密码
	 */
	private String emailPassword;

	/**
	 * 邀请人ID
	 */
	private String inviteId;

	/**
	 * 公司
	 */
	@Length(max = 64, message = "公司长度不能超过64位！")
	private String dection;

	/**
	 * 公司ID
	 */
	@TableField(exist = false)
	private String orgId;

	/**
	 * 用户状态：0-正常，1-禁用
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

	public User(
			@NotBlank(message = "账号不能为空！") @Pattern(regexp = "0?(13|14|15|17|18|19)[0-9]{9}", message = "账号不合法！") String username,
			String password, @NotBlank(message = "姓名不能为空！") @Length(max = 12, message = "姓名长度不能超过12位！") String realName,
			String avatar, String email, String inviteId) {
		super();
		this.username = username;
		this.password = password;
		this.realName = realName;
		this.avatar = avatar;
		this.email = email;
		this.inviteId = inviteId;
	}

}
