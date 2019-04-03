package com.suizhu.common.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.suizhu.common.constant.HttpConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R {

	private int status;

	private String msg;

	@JsonInclude(Include.NON_NULL)
	private Object data;

	public static R ok() {
		return R.builder().status(HttpConstant.OK).msg("OK").build();
	}

	public static R ok(Object data) {
		return R.builder().status(HttpConstant.OK).msg("OK").data(data).build();
	}

	public static R error() {
		return R.builder().status(HttpConstant.NOT_IMPLEMENTED).msg("操作失败！").build();
	}

	public static R error(int status) {
		return R.builder().status(status).msg("操作失败！").build();
	}

	public static R error(String msg) {
		return R.builder().status(HttpConstant.NOT_IMPLEMENTED).msg(msg).build();
	}

	public static R error(int status, String msg) {
		return R.builder().status(status).msg(msg).build();
	}

}
