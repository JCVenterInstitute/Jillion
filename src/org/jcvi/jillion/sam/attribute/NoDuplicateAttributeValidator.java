package org.jcvi.jillion.sam.attribute;

import org.jcvi.jillion.sam.SamAttributed;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@link SamAttributeValidator} that makes sure
 * that any records being validated don't contain
 * duplicate key-value pairs.
 * <p>
 * The Sam specification says that there shouldn't be any duplicates:
 * Section 1.5 : <q>Each TAG can only appear once in one alignment line.</q>
 * but some program produce SAM and BAM files that have duplicated records. * 
 * </p>
 * 
 * @author dkatzel
 *
 */
public enum NoDuplicateAttributeValidator implements SamAttributeValidator{

	INSTANCE;

	@Override
	public void validate(SamHeader header, SamAttributed record, SamAttribute attribute)
			throws InvalidAttributeException {
		if(record.hasAttribute(attribute.getKey())){
			throw new InvalidAttributeException(record +" has duplicate key : " + attribute.getKey());
		}
		
	}
	
}
