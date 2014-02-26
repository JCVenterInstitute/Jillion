package org.jcvi.jillion.sam;

public class SamValidationException extends Exception{

	private static final long serialVersionUID = 1L;

	public SamValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SamValidationException(String message) {
		super(message);
	}

}
