package org.jcvi.jillion.sam.attribute;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.sam.header.SamHeader;

public class ChainedSamAttributeValidatorBuilder {

	
	private final List<SamAttributeValidator> validators = new ArrayList<SamAttributeValidator>();
	
	public ChainedSamAttributeValidatorBuilder(){
		this(true);
	}
	
	public ChainedSamAttributeValidatorBuilder(boolean validateReservedAttributes){
		if(validateReservedAttributes){
			validators.add(ReservedAttributeValidator.INSTANCE);
		}
	}
	
	public ChainedSamAttributeValidatorBuilder addValidator(SamAttributeValidator validator){
		if(validator ==null){
			throw new NullPointerException("validator can not be null");
		}
		validators.add(validator);
		return this;
	}
	
	public SamAttributeValidator build(){
		return new ChainedSamAttributeValidator(validators);
	}
	
	
	private static class ChainedSamAttributeValidator implements SamAttributeValidator{
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
