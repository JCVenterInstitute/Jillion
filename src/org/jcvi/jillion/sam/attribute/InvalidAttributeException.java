package org.jcvi.jillion.sam.attribute;

public class InvalidAttributeException extends Exception{

	
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
