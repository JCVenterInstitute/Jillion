package org.jcvi.jillion.sam.attribute;

import org.jcvi.jillion.sam.header.SamHeader;

public interface SamAttributeValidator {

	void validate(SamHeader header, SamAttribute attribute) throws InvalidAttributeException;
}
