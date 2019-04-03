package com.suizhu.common.exception;

import com.suizhu.common.constant.HttpConstant;

import lombok.Getter;

/**
 * 自定义异常信息
 * 
 * @author gaochao
 * @date Feb 19, 2019
 */
@Getter
public class MyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7592813637612466000L;

	private int status = HttpConstant.NOT_IMPLEMENTED;

	private String msg;

	public MyException(int status, String msg) {
		super();
		this.status = status;
		this.msg = msg;
	}

	public MyException(String msg) {
		super();
		this.msg = msg;
	}

}
