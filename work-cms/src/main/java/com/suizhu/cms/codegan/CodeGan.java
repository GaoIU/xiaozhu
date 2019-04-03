package com.suizhu.cms.codegan;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CodeGan {

	@NotNull(message = "数据库表不能为空！")
	@NotEmpty(message = "数据库表不能为空！")
	private String[] tableNames;

	@NotBlank(message = "模块名不能为空！")
	private String modelName;

	@NotBlank(message = "包名不能为空！")
	private String parentName;

	@NotBlank(message = "作者名不能为空！")
	private String author;

	private String tablePrefix;

	@NotBlank(message = "要保存的目录路径不能空！")
	private String dir;

}
