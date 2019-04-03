package com.suizhu.work.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工程项目记录音频文件表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FileAudio extends Model<FileAudio> {

	private static final long serialVersionUID = 1L;

	/** 状态：0-临时文件 */
	public static final int STATUS_TEMP = 0;

	/** 状态：1-永久文件 */
	public static final int STATUS_PERM = 1;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 工程项目记录ID
	 */
	private String buildProjectLogId;

	/**
	 * 音频文件ID
	 */
	private String fileId;

	/**
	 * 音频文件地址
	 */
	private String fileUrl;

	/**
	 * 文件时长
	 */
	private String times;

	/**
	 * 状态：0-临时文件，1-永久文件
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

	public FileAudio(String userId, String fileId, String fileUrl, String times) {
		super();
		this.userId = userId;
		this.fileId = fileId;
		this.fileUrl = fileUrl;
		this.times = times;
	}

}
