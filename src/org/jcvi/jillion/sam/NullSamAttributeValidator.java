package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.attribute.InvalidAttributeException;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code NullSamAttributeValidator}
 * doesn't do any validation,
 * everything passes.
 * @author dkatzel
 *
 */
enum NullSamAttributeValidator implements SamAttributeValidator{
	INSTANCE;

	@Override
	public void validate(SamHeader header, SamAttribute attribute)
			throws InvalidAttributeException {
		//no-op
		
	}
	
}
