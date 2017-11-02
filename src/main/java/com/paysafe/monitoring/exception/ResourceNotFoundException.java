package com.paysafe.monitoring.exception;

public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 475760332563297383L;

	public ResourceNotFoundException(String s) {
		super(s);
	}

}
