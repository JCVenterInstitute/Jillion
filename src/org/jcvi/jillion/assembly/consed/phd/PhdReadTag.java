/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
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

import java.util.Date;

import org.jcvi.jillion.core.Range;
/**
 * {@code PhdReadTag} is specially
 * formatted optional additional information
 * about a particular range of bases of a phd record.
 * @author dkatzel
 *
 */
public interface PhdReadTag {
	/**
	 * Get the type of read tag this is.
	 * @return a free format String describing
	 * the type of tag; will never be null.
	 * The type will usually not be empty
	 * but that can't be guaranteed.
	 */
	String getType();
	/**
	 * Get the type of source program that generated
	 * this tag.
	 * @return a free format String describing
	 * the program that generated this tag
	 * (usually the program name); will never be null.
	 * The source will usually not be empty
	 * but that can't be guaranteed.
	 */
	String getSource();
	/**
	 * Get the ungapped {@link Range}
	 * into the read that this tag refers.
	 * @return a Range; will never be null.
	 */
	Range getUngappedRange();
	/**
	 * Get the {@link Date}
	 * that this tag was generated. 
	 * @return a new {@link Date}
	 * will never be null.
	 */
	Date getDate();
	/**
	 * Get an optional comment for this tag.
	 * @return a String (possibly multi-line)
	 * for this comment; or {@code null}
	 * if this tag does not have a comment.
	 */
	String getComment();
	/**
	 * Get an optional free form data for this tag.
	 * @return a String (possibly multi-line)
	 * for this free form data; or {@code null}
	 * if this tag does not have any free form data.
	 */
	String getFreeFormData();

}
