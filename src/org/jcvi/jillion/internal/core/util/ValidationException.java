package org.jcvi.jillion.internal.core.util;

public class ValidationException extends IllegalStateException {

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationException(String s) {
		super(s);
	}

}
