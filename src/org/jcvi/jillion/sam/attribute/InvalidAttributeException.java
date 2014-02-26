package org.jcvi.jillion.sam.attribute;

import org.jcvi.jillion.sam.SamValidationException;

public class InvalidAttributeException extends SamValidationException{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3235986760636350325L;

	public InvalidAttributeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidAttributeException(String message) {
		super(message);
	}

}
