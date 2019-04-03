package com.suizhu.work.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工程模版明细
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class ModelBuild extends Model<ModelBuild> {

	private static final long serialVersionUID = 1L;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 项目名称
	 */
	private String name;

	/**
	 * 计划天数
	 */
	private Integer days;

	/**
	 * 序号
	 */
	private Integer sort;

	/**
	 * 相对开始天数
	 */
	private Integer beginDay;

	/**
	 * 模版ID
	 */
	private String modelId;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public ModelBuild(String name, Integer days, Integer sort, Integer beginDay, String modelId) {
		super();
		this.name = name;
		this.days = days;
		this.sort = sort;
		this.beginDay = beginDay;
		this.modelId = modelId;
	}

}
