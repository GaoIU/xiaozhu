package com.suizhu.cms.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 后台用户 - 后台角色 关联表
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class SysUserRole extends Model<SysUserRole> {

	private static final long serialVersionUID = 1L;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 后台用户ID
	 */
	private String sysUserId;

	/**
	 * 后台角色ID
	 */
	private String sysRoleId;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public SysUserRole(String sysUserId, String sysRoleId) {
		super();
		this.sysUserId = sysUserId;
		this.sysRoleId = sysRoleId;
	}

}
