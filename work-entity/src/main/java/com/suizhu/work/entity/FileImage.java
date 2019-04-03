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
 * 工程项目记录图片文件表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-06
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FileImage extends Model<FileImage> {

	private static final long serialVersionUID = 1L;

	/** 图片类型：0-门店相册图片 */
	public static final int TYPE_ABLUM = 0;

	/** 图片类型：1-工程图纸 */
	public static final int TYPE_ENGINER = 1;

	/** 图片类型：2-工程项目日志图片 */
	public static final int TYPE_PROJECT = 2;

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
	 * 门店ID
	 */
	private String doorwayId;

	/**
	 * 工程项目记录ID
	 */
	private String buildProjectLogId;

	/**
	 * 图片文件ID
	 */
	private String fileId;

	/**
	 * 图片文件地址
	 */
	private String fileUrl;

	/**
	 * 图片类型：0-门店相册图片，1-工程图纸，2-工程项目日志图片
	 */
	private Integer type;

	/**
	 * 状态：0-临时文件，1-永久文件
	 */
	private Integer status = STATUS_TEMP;

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

	public FileImage(String userId, String doorwayId, String fileId, String fileUrl) {
		super();
		this.userId = userId;
		this.doorwayId = doorwayId;
		this.fileId = fileId;
		this.fileUrl = fileUrl;
	}

}
