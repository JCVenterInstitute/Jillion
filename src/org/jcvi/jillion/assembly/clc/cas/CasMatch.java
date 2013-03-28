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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;


import org.jcvi.jillion.core.Range;

public interface CasMatch {

    boolean matchReported();
    boolean readHasMutlipleMatches();
    boolean hasMultipleAlignments();
    boolean readIsPartOfAPair();
    CasAlignment getChosenAlignment();
    long getNumberOfMatches();
    long getNumberOfReportedAlignments();
    int getScore();
    /**
     * Get the Range of the read used
     * in the CLC mapping.
     * Sometimes, the input read has been processed 
     * before CLC mapped the data. 
     * @return a {@link Range} representing the good region
     * of the full length read that was included in the mapping assembly;
     * or {@code null} if no trim range exists or is known.
     */
    Range getTrimRange();
    
}
