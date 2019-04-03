package com.suizhu.work.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.suizhu.common.constant.HttpConstant;
import com.suizhu.common.exception.BadRequestException;
import com.suizhu.common.exception.ForbiddenException;
import com.suizhu.common.exception.MyException;
import com.suizhu.common.exception.NotfoundException;
import com.suizhu.common.exception.UnauthorizedException;

/**
 * 异常捕获
 * 
 * @author gaochao
 * @date Mar 1, 2019
 */
//@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

//	@ExceptionHandler(value = Exception.class)
	// @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//	public Map<String, Object> handlerException(Exception e) {
//		String message = ExceptionUtil.getMessage(e);
//		log.error("System is error: {}", message);
//
//		Map<String, Object> map = new HashMap<String, Object>(2);
//		map.put("status", HttpConstant.INTERNAL_SERVER_ERROR);
//		map.put("msg", "服务异常，请联系客服人员处理！");
//		return map;
//	}

	@ExceptionHandler(value = BindException.class)
	// @ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, Object> handlerBindException(BindException be) {
		String msg = be.getBindingResult().getFieldError().getDefaultMessage();
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("status", HttpConstant.BAD_REQUEST);
		map.put("msg", msg);
		return map;
	}

	@ExceptionHandler(value = BadRequestException.class)
	// @ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, Object> handlerBadRequestException(BadRequestException bre) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("status", bre.getStatus());
		map.put("msg", bre.getMsg());
		return map;
	}

	@ExceptionHandler(value = ForbiddenException.class)
	// @ResponseStatus(HttpStatus.FORBIDDEN)
	public Map<String, Object> handlerForbiddenException(ForbiddenException fe) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("status", fe.getStatus());
		map.put("msg", fe.getMsg());
		return map;
	}

	@ExceptionHandler(value = MyException.class)
	// @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
	public Map<String, Object> handlerMyException(MyException me) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("status", me.getStatus());
		map.put("msg", me.getMsg());
		return map;
	}

	@ExceptionHandler(value = NotfoundException.class)
	// @ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, Object> handlerNotfoundException(NotfoundException ne) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("status", ne.getStatus());
		map.put("msg", ne.getMsg());
		return map;
	}

	@ExceptionHandler(value = UnauthorizedException.class)
	// @ResponseStatus(HttpStatus.UNAUTHORIZED)
	public Map<String, Object> handlerUnauthorizedException(UnauthorizedException ue) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("status", ue.getStatus());
		map.put("msg", ue.getMsg());
		return map;
	}

}
