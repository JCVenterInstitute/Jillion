/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
