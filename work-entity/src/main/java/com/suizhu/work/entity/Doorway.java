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
 * 门店表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Doorway extends Model<Doorway> {

	private static final long serialVersionUID = 1L;

	/** 异常筛选：1-预警 */
	public static final int EP_EARLY = 1;

	/** 异常筛选：2-超时 */
	public static final int EP_OVERTIME = 2;

	/** 门店状态：0-未开始 */
	public static final int STATUS_NOT_START = 0;

	/** 门店状态：1-进行中 */
	public static final int STATUS_DOING = 1;

	/** 门店状态：2-已完成 */
	public static final int STATUS_FLUSH = 2;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 门店名称
	 */
	@NotBlank(message = "门店名称不能为空！")
	@Length(max = 128, message = "门店名称长度不能超过128位！")
	private String name;

	/**
	 * 合同房量
	 */
	@NotNull(message = "合同房量不能为空！")
	private Integer houseNum;

	/**
	 * 省份
	 */
	@NotBlank(message = "省份不能为空！")
	@Length(max = 16, message = "省份长度不能超过16位！")
	private String province;

	/**
	 * 城市
	 */
	@NotBlank(message = "城市不能为空！")
	@Length(max = 16, message = "城市长度不能超过16位！")
	private String city;

	/**
	 * 区域
	 */
	@NotBlank(message = "区域不能为空！")
	@Length(max = 16, message = "区域长度不能超过16位！")
	private String area;

	/**
	 * 详细地址
	 */
	@NotBlank(message = "详细地址不能为空！")
	@Length(max = 200, message = "详细地址长度不能超过200位！")
	private String address;

	/**
	 * 门店类型ID
	 */
	@NotBlank(message = "门店类型不能为空！")
	private String categoryId;
	
	/**
	 * 门店封面
	 */
	private String cover;

	/**
	 * 所属公司ID
	 */
	private String orgId;

	/**
	 * 门店状态：0-未开始，1-进行中，2-已完成
	 */
	private Integer status = STATUS_NOT_START;

	/**
	 * 创建人ID
	 */
	private String createId;

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
	 * 模版ID
	 */
	@NotBlank(message = "模版ID不能为空！")
	@TableField(exist = false)
	private String modelIds;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
