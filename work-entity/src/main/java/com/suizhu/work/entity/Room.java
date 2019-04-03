package com.suizhu.work.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 房间表
 * </p>
 *
 * @author GaoChao
 * @since 2019-03-07
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Room extends Model<Room> {

	private static final long serialVersionUID = 1L;

	/**
	 * 开发主键
	 */
	private String id;

	/**
	 * 房间名称
	 */
	@NotBlank(message = "房间名称不能为空！")
	private String name;

	/**
	 * 楼层ID
	 */
	@NotBlank(message = "楼层ID不能为空！")
	private String floorId;

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

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	public Room(@NotBlank(message = "房间名称不能为空！") String name, @NotBlank(message = "楼层ID不能为空！") String floorId,
			String createId) {
		super();
		this.name = name;
		this.floorId = floorId;
		this.createId = createId;
	}

}
