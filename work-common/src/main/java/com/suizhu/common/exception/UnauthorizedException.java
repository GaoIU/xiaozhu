package com.suizhu.common.exception;

import com.suizhu.common.constant.HttpConstant;

import lombok.Getter;

/**
 * 401异常
 * 
 * @author gaochao
 * @date Feb 19, 2019
 */
@Getter
public class UnauthorizedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1952673156267507824L;

	private int status;

	private String msg;

	public UnauthorizedException() {
		super();
		this.status = HttpConstant.UNAUTHORIZED;
		this.msg = "请验证身份信息";
	}

	public UnauthorizedException(String msg) {
		super();
		this.status = HttpConstant.UNAUTHORIZED;
		this.msg = msg;
	}

}