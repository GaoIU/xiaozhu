package com.suizhu.work.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 短信发送日志表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LogSms extends Model<LogSms> {

	private static final long serialVersionUID = 1L;

	/** 类型：0-修改密码 */
	public static final int TYPE_RESTPWD = 0;

	/** 类型：1-邀请消息通知 */
	public static final int TYPE_INVITE = 1;

	/** 结果：0-成功 */
	public static final int RESULT_SECCUSS = 0;

	/** 结果：1-失败 */
	public static final int RESULT_ERROR = 1;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 类型：0-修改密码，1-邀请消息通知
	 */
	private Integer type;

	/**
	 * 结果：0-成功，1-失败
	 */
	private Integer result;

	/**
	 * 返回消息
	 */
	private String body;

	/**
	 * 发送内容
	 */
	private String content;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@Override
	protected Serializable pkVal() {
		return null;
	}

}
