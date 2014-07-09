package org.jcvi.jillion.internal.core.util;

import java.util.function.Function;
import java.util.function.Predicate;

public final class Validator {

	public static <T> void validate(T value, Predicate<T> validation, Function<T, String> message) throws ValidationException{
		if(value ==null){
			throw new NullPointerException();
		}
		if(!validation.test(value)){
			throw new ValidationException(message.apply(value));
		}
	}
}
