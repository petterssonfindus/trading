package com.algotrading.util;

import org.springframework.http.HttpStatus;

public class RestApplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private HttpStatus httpStatus;

	public RestApplicationException(final String message, final HttpStatus pHttpStatus) {
		super(message);
		httpStatus = pHttpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " (HTTP-Status=" + httpStatus.value() + ", " + httpStatus.getReasonPhrase() + ")";
	}

	/**
	 * Orginal-Message holen
	 */
	public String getOrginalMessage() {
		return super.getMessage();
	}

}