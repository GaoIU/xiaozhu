package com.suizhu.common.exception;

import com.suizhu.common.constant.HttpConstant;

import lombok.Getter;

/**
 * 400异常
 * 
 * @author gaochao
 * @date Mar 1, 2019
 */
@Getter
public class BadRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4370725490830785117L;

	private int status;

	private String msg;

	public BadRequestException() {
		super();
		this.status = HttpConstant.BAD_REQUEST;
		this.msg = "请求语义或请求参数有误";
	}

	public BadRequestException(String msg) {
		super();
		this.status = HttpConstant.BAD_REQUEST;
		this.msg = msg;
	}

}
