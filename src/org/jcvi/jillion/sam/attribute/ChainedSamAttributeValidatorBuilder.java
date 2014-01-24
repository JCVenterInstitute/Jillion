package org.jcvi.jillion.sam.attribute;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code ChainedSamAttributeValidatorBuilder}
 * builds a {@link SamAttributeValidator}
 * that calls several other
 * {@link SamAttributeValidator}s
 * in a chain.  If a validator
 * "passes" an attribute, then the next validator
 * in the chain gets called etc.
 * 
 * The attribute only passes the chained validator
 * if all the validators in the chain also pass the attribute.
 * If any validator throws an exception, then that exception is thrown
 * by the chain and no other validators in the chain are called.
 * 
 * @author dkatzel
 *
 */
public class ChainedSamAttributeValidatorBuilder {

	
	private final List<SamAttributeValidator> validators = new ArrayList<SamAttributeValidator>();
	/**
	 * Convenience constructor same as {@link #ChainedSamAttributeValidatorBuilder(boolean)
	 * new ChainedSamAttributeValidatorBuilder(true)}.
	 * @see #ChainedSamAttributeValidatorBuilder(boolean)
	 */
	public ChainedSamAttributeValidatorBuilder(){
		this(true);
	}
	/**
	 * Create a new ChainedSamAttributeValidatorBuilder instance.
	 * 
	 * @param validateReservedAttributes should the rules for the {@link ReservedSamAttributeKeys} 
	 * specified in the SAM format be checked.  If {@code true}, then the first {@link SamAttributeValidator}
	 * in the chain will check to make sure the values in the attributes meet the requirements specified 
	 * in the SAM format specification.
	 */
	public ChainedSamAttributeValidatorBuilder(boolean validateReservedAttributes){
		if(validateReservedAttributes){
			validators.add(ReservedAttributeValidator.INSTANCE);
		}
	}
	/**
	 * Add the given {@link SamAttributeValidator} to the end of the chain.
	 * @param validator the {@link SamAttributeValidator} to add;
	 * can not be null.
	 * @return this.
	 * @throws NullPointerException if validator is null.
	 */
	public ChainedSamAttributeValidatorBuilder addValidator(SamAttributeValidator validator){
		if(validator ==null){
			throw new NullPointerException("validator can not be null");
		}
		validators.add(validator);
		return this;
	}
	/**
	 * Create a new {@link SamAttributeValidator} instance
	 * that will delegate to  all the validators in the chain
	 * so far.
	 * @return a new {@link SamAttributeValidator}
	 * instance; will never be null.
	 */
	public SamAttributeValidator build(){
		return new ChainedSamAttributeValidator(validators);
	}
	
	
	static final class ChainedSamAttributeValidator implements SamAttributeValidator{
		private final List<SamAttributeValidator> validators;

		public ChainedSamAttributeValidator(
				List<SamAttributeValidator> validators) {
			//defensive copy
			this.validators = new ArrayList<SamAttributeValidator>(validators);
		}

		@Override
		public void validate(SamHeader header, SamAttribute attribute)
				throws InvalidAttributeException {
			for(SamAttributeValidator validator : validators){
				validator.validate(header, attribute);
			}
			
		}
		
		
	}
	
	
	
}
