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
package org.jcvi.jillion.sam.header;
/**
 * {@code SamReferenceSequence} is an object
 * representation of the metadata for a 
 * reference used in a SAM or BAM file.
 * 
 * @author dkatzel
 *
 */
public interface SamReferenceSequence {

    /**
     * Get the human readable name of this reference sequence.
     * @return a String; will never be null.
     */
    String getName();

    /**
     * Get the number of bases in this reference sequence.
     * @return the number of bases; will always be > 0.
     */
    int getLength();

    String getGenomeAssemblyId();

    String getSpecies();

    String getUri();

    String getMd5();

}
