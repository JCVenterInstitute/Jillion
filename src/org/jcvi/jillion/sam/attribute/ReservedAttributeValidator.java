/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
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
