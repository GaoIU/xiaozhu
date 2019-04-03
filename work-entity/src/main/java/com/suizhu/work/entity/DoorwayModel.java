package com.suizhu.work.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 门店表 - 模版表 中间关联表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-05
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DoorwayModel extends Model<DoorwayModel> {

	private static final long serialVersionUID = 1L;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 门店ID
	 */
	private String doorwayId;

	/**
	 * 分类ID
	 */
	private String categoryId;

	/**
	 * 模版ID
	 */
	private String modelId;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public DoorwayModel(String doorwayId, String categoryId, String modelId) {
		super();
		this.doorwayId = doorwayId;
		this.categoryId = categoryId;
		this.modelId = modelId;
	}

}
