/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.phd;

import java.util.List;
/**
 * {@code PhdWholeReadItem} is specially
 * formatted optional additional information
 * about an entire phd record.  WholeReadItems can be 
 * used by Phrap/Consed to process reads differently.
 * For example, some whole read items designate which 
 * reads are mate pairs or which reads have faked data.
 * 
 * WholeReadItems contain free form text that will vary
 * by what kind of data is provided and which program
 * is producing or consuming these tags so a generic
 * {@link PhdWholeReadItem} object can only provide 
 * the free form data as Strings.
 * @author dkatzel
 *
 */
public interface PhdWholeReadItem {
	/**
	 * Get a list of all the lines
	 * contained in this WholeReadItem.
	 * Each String may still contain whitespace or end of line
	 * data.
	 * @return a list of Strings, one for each line
	 * in the read item; if no lines exist for this 
	 * item, then an empty list will be returned;
	 * will never return null.
	 */
	List<String> getLines();
	
	
}
