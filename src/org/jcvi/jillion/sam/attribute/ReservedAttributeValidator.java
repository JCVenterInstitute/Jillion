/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
