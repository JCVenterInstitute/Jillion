package org.jcvi.jillion.sam.attribute;

import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code ReservedAttributeValidator}
 * is a {@link SamAttributeValidator}
 * that will enforce that the types
 * for any reserved {@link SamAttributeKey}s
 * are the correct type  and contain valid 
 * values for that type.  Any non-reserved
 * key is assumed to be valid and is not validated.
 * 
 * @author dkatzel
 *
 */
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
