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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.List;
/**
 * {@code ConsensusAceTag} is an {@link AceTag}
 * that maps to a particular location on the 
 * consensus of a contig in an ace file.
 * @author dkatzel
 */
public interface ConsensusAceTag extends RangeableAceTag {
    
    /**
     * Get the Id of contig
     * this tag references.
     */
    String getId();
    /**
     * Get any comments (if any) associated with this
     * tag.
     * @return a Set of all the comments (in case there are 
     * multiple comments).  Each comment string may contain
     * multiple lines, if there are no comments,
     * then the list is empty; never null.
     */
    List<String> getComments();
}
