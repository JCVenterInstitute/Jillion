package org.jcvi.jillion.sam.attribute;

import org.jcvi.jillion.sam.header.SamHeader;

public enum ReservedAttributeValidator implements SamAttributeValidator{

	INSTANCE;
	
	@Override
	public void validate(SamHeader header, SamAttribute attribute)
			throws InvalidAttributeException {
		ReservedSamAttributeKeys reserved = ReservedSamAttributeKeys.parseKey(attribute.getKey());
		if(reserved !=null){
			//is reserved
			reserved.validate(header, attribute.getValue());
		}
		
	}

}
