/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.contig.ace;

import static org.junit.Assert.*;


import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;

/**
 * @author dkatzel
 *
 *
 */
public final class AceContigTestUtil {

   

    
    public static  void assertContigParsedCorrectly(Contig<PlacedRead> expected, Contig<? extends PlacedRead> actual) {
        assertEquals(expected.getId(), actual.getId()); 
        assertEquals(expected.getConsensus().asList(), actual.getConsensus().asList());
        assertEquals(expected.getId(),expected.getNumberOfReads(), actual.getNumberOfReads());
        for(PlacedRead expectedRead : expected.getPlacedReads()){
            assertPlacedReadParsedCorrectly(expectedRead, actual.getPlacedReadById(expectedRead.getId()));
        }
        
    }

    public static  void assertPlacedReadParsedCorrectly(PlacedRead expected,
            PlacedRead actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStart(), actual.getStart());
        assertEquals(expected.getEnd(), actual.getEnd());
        assertEquals(expected.getLength(), actual.getLength());
        assertEquals(expected.getId(),expected.getValidRange(), actual.getValidRange());
        assertEquals(expected.getNucleotideSequence().asList(), actual.getNucleotideSequence().asList());
        
    }
}
