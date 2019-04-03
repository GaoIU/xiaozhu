package com.suizhu.work.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 筹建提示通知
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-08
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BuildTip extends Model<BuildTip> {

	private static final long serialVersionUID = 1L;

	/** 提醒开关：0-开启 */
	public static final int REMIND_OPEN = 0;

	/** 提醒开关：1-关闭 */
	public static final int REMIND_CLOSE = 0;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 筹建工程ID
	 */
	private String buildEnginerId;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 提醒开关：0-开启，1-关闭
	 */
	private Integer remind = REMIND_OPEN;

	/**
	 * 公司ID
	 */
	private String orgId;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public BuildTip(String buildEnginerId, String userId, String orgId) {
		super();
		this.buildEnginerId = buildEnginerId;
		this.userId = userId;
		this.orgId = orgId;
	}

}
