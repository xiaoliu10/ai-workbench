package com.aiworkbench.backend.exception;

import com.aiworkbench.backend.common.BizException;
import com.aiworkbench.backend.common.R;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BizException.class)
	@ResponseStatus(HttpStatus.OK)
	public R<Void> handleBizException(BizException e) {
		log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
		return R.fail(e.getCode(), e.getMessage());
	}

	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public R<Void> handleBadCredentialsException(BadCredentialsException e) {
		return R.unauthorized("用户名或密码错误");
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public R<Void> handleAuthenticationException(AuthenticationException e) {
		return R.unauthorized(e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R<Void> handleValidationException(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.reduce((a, b) -> a + "; " + b)
				.orElse("参数校验失败");
		return R.badRequest(message);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public R<Void> handleConstraintViolationException(ConstraintViolationException e) {
		return R.badRequest(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public R<Void> handleException(Exception e) {
		log.error("系统异常", e);
		return R.fail("系统内部错误，请稍后重试");
	}
}
