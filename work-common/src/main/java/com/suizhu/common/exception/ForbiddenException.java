package com.suizhu.common.exception;

import com.suizhu.common.constant.HttpConstant;

import lombok.Getter;

/**
 * 403异常
 * 
 * @author gaochao
 * @date Feb 19, 2019
 */
@Getter
public class ForbiddenException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1471446251184701190L;

	private int status;

	private String msg;

	public ForbiddenException() {
		super();
		this.status = HttpConstant.FORBIDDEN;
		this.msg = "无权操作！";
	}

	public ForbiddenException(String msg) {
		super();
		this.status = HttpConstant.FORBIDDEN;
		this.msg = msg;
	}

}
