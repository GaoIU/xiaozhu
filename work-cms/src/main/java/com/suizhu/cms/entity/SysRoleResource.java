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
 * 后台角色 - 后台资源 关联表
 * </p>
 *
 * @author GaoChao
 * @since 2019-02-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
public class SysRoleResource extends Model<SysRoleResource> {

	private static final long serialVersionUID = 1L;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 后台角色ID
	 */
	private String sysRoleId;

	/**
	 * 后台资源ID
	 */
	private String sysResourceId;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public SysRoleResource(String sysRoleId, String sysResourceId) {
		super();
		this.sysRoleId = sysRoleId;
		this.sysResourceId = sysResourceId;
	}

}
