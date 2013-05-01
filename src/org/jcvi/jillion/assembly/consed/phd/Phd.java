/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Mar 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.phd;

import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.trace.Trace;
/**
 * {@code Phd} is an object representation
 * of a single read from a phd file or a phd.ball file.
 * @author dkatzel
 *
 */
public interface Phd extends Trace {

	/**
	 * Get the optional comments associated
	 * with this phd.
	 * @return a {@link Map} where
	 * the keys and values are both 
	 * Strings; if this phd does not
	 * contain any comments, then
	 * this method returns an empty map.
	 * Will never return null.
	 */
    Map<String,String> getComments();
    /**
	 * Get the optional whole read items associated
	 * with this phd that are contained in the phd file.
	 * @return a {@link List} of {@link PhdWholeReadItem}s
	 * if this phd does not
	 * contain any whole read items, then
	 * this method returns an empty list.
	 * Will never return null.
	 */
    List<PhdWholeReadItem> getWholeReadItems();
    /**
   	 * Get the optional whole read tags associated
   	 * with this phd that are contained in the phd file.
   	 * @return a {@link List} of {@link PhdReadTag}s
   	 * if this phd does not
   	 * contain any whole read items, then
   	 * this method returns an empty list.
   	 * Will never return null.
   	 */
    List<PhdReadTag> getReadTags();
    /**
     * Get the optional {@link PositionSequence}
     * for this Phd.  Recent versions of consed
     * allow the position sequence to be optional
     * if the sequence technology does not support it
     * (previous versions of consed forced the positions
     * to be faked).  For example, solexa sequences
     * do not have positions, however sanger chromatograms
     * do.
     * @return the {@link PositionSequence} for this
     * phd if there is one; or null
     * if no position sequence exists
     * for this trace.
     */
    PositionSequence getPositionSequence();
}
