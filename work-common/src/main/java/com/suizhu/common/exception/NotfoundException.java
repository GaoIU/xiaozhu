package com.suizhu.common.exception;

import com.suizhu.common.constant.HttpConstant;

import lombok.Getter;

/**
 * 404异常
 * 
 * @author gaochao
 * @date Feb 19, 2019
 */
@Getter
public class NotfoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7959116886160937447L;

	private int status;

	private String msg;

	public NotfoundException() {
		super();
		this.status = HttpConstant.NOT_FOUND;
		this.msg = "无效访问";
	}

	public NotfoundException(String msg) {
		super();
		this.status = HttpConstant.NOT_FOUND;
		this.msg = msg;
	}

}
