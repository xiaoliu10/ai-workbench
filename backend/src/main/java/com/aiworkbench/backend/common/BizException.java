package com.aiworkbench.backend.common;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BizException extends RuntimeException {

	private final int code;

	public BizException(String message) {
		super(message);
		this.code = 500;
	}

	public BizException(int code, String message) {
		super(message);
		this.code = code;
	}

	public static BizException badRequest(String message) {
		return new BizException(400, message);
	}

	public static BizException unauthorized(String message) {
		return new BizException(401, message);
	}

	public static BizException forbidden(String message) {
		return new BizException(403, message);
	}

	public static BizException notFound(String message) {
		return new BizException(404, message);
	}
}
