package com.aiworkbench.backend.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> {

	private int code;
	private String message;
	private T data;

	public static <T> R<T> ok() {
		return new R<>(0, "success", null);
	}

	public static <T> R<T> ok(T data) {
		return new R<>(0, "success", data);
	}

	public static <T> R<T> ok(String message, T data) {
		return new R<>(0, message, data);
	}

	public static <T> R<T> fail(String message) {
		return new R<>(500, message, null);
	}

	public static <T> R<T> fail(int code, String message) {
		return new R<>(code, message, null);
	}

	public static <T> R<T> unauthorized(String message) {
		return new R<>(401, message, null);
	}

	public static <T> R<T> forbidden(String message) {
		return new R<>(403, message, null);
	}

	public static <T> R<T> badRequest(String message) {
		return new R<>(400, message, null);
	}

	public static <T> R<T> notFound(String message) {
		return new R<>(404, message, null);
	}
}
