package com.suizhu.work.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表 - 门店表 中间关联表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserDoorway extends Model<UserDoorway> {

	private static final long serialVersionUID = 1L;

	/** 外协：0-是 */
	public static final int WORKOUT_YES = 0;

	/** 外协：1-否 */
	public static final int WORKOUT_NOT = 1;

	/** 类型：0-工程经理 */
	public static final int TYPE_DOORWAY = 0;

	/** 类型：1-项目组长 */
	public static final int TYPE_PROJECT = 1;

	/** 类型：2-普通员工 */
	public static final int TYPE_NORMAL = 2;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 名称
	 */
	@NotBlank(message = "姓名不能为空！")
	@Length(max = 12, message = "姓名长度不能超过12位！")
	private String name;

	/**
	 * 手机号码
	 */
	@NotBlank(message = "手机号码不能为空！")
	@Pattern(regexp = "0?(13|14|15|17|18|19)[0-9]{9}", message = "手机号码不合法！")
	private String mobile;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 工号
	 */
	@Length(max = 16, message = "工号长度不能超过16位！")
	private String workNum;

	/**
	 * 工种
	 */
	@Length(max = 8, message = "工种长度不能超过8位！")
	private String workKind;

	/**
	 * 外协：0-是，1-否
	 */
	@NotNull(message = "请选择是否为外协！")
	private Integer workOut = WORKOUT_NOT;

	/**
	 * 部门
	 */
	@NotBlank(message = "部门不能为空！")
	@Length(max = 8, message = "部门长度不能超过8位！")
	private String dep;

	/**
	 * 职位
	 */
	@Length(max = 8, message = "职位长度不能超过8位！")
	private String pos;

	/**
	 * 类型：0-工程经理，1-项目组长，2-普通员工
	 */
	private Integer type;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 门店ID
	 */
	private String doorwayId;

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
