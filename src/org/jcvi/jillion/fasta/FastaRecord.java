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
/**
 * 
 */
package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Sequence;



/**
 * {@code FastaRecord} is an interface for interacting
 * with a single FASTA record.
 * @param <T> the type used as the value of the record
 * @author jsitz
 * @author dkatzel
 */
public interface FastaRecord<S,T extends Sequence<S>>
{

	 /**
     * Get the Id of this record.
     * @return A <code>String</code>.
     */
    String getId();

    /**
     * Get the comment (if any) associated with this record.
     * @return A <code>String</code> of the comment
     * or {@code null} if there is no comment.
     */
    String getComment();
    /**
     * Get the Sequence associated with this record.
     * @return a Sequence, never null.
     */
    T getSequence();
    /**
     * Two FastaRecords are equal
     * if they both have the same id
     * and the same sequence.
     */
    @Override
    boolean equals(Object o);
    
    @Override
    int hashCode();
}
