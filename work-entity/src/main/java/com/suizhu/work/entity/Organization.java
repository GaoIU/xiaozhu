package com.suizhu.work.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 公司组织表
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class Organization extends Model<Organization> {

	private static final long serialVersionUID = 1L;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 公司名称
	 */
	@NotBlank(message = "公司名称不能为空")
	@Length(max = 64, message = "公司名称长度不能超过64位！")
	private String name;

	/**
	 * 门店使用数量
	 */
	@NotNull(message = "门店可使用数量不能为空！")
	private Integer doorwayNum;

	/**
	 * 到期时间
	 */
	@NotNull(message = "到期时间不能为空！")
	@Future(message = "到期时间必须大于当前时间")
	private LocalDate expireTime;

	/**
	 * 上级ID
	 */
	private String parentId;

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

	public Organization(@NotBlank(message = "公司名称不能为空") @Length(max = 64, message = "公司名称长度不能超过64位！") String name,
			@NotNull(message = "门店可使用数量不能为空！") Integer doorwayNum,
			@NotNull(message = "到期时间不能为空！") @Future(message = "到期时间必须大于当前时间") LocalDate expireTime) {
		super();
		this.name = name;
		this.doorwayNum = doorwayNum;
		this.expireTime = expireTime;
	}

}
