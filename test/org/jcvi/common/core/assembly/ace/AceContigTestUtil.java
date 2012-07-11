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

package org.jcvi.common.core.assembly.ace;

import static org.junit.Assert.*;


import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class AceContigTestUtil {

   

    
    public static  void assertContigsEqual(Contig<? extends AssembledRead> expected, Contig<? extends AssembledRead> actual) {
        assertEquals(expected.getId(), actual.getId()); 
        assertEquals(expected.getConsensus().asList(), actual.getConsensus().asList());
        assertEquals(expected.getId(),expected.getNumberOfReads(), actual.getNumberOfReads());
        CloseableIterator<? extends AssembledRead> iter = null;
        try{
        	iter = expected.getReadIterator();
        	while(iter.hasNext()){
        		AssembledRead expectedRead = iter.next();
        		assertPlacedReadParsedCorrectly(expectedRead, actual.getRead(expectedRead.getId()));
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        
    }

    public static  void assertPlacedReadParsedCorrectly(AssembledRead expected,
            AssembledRead actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getId(), expected.getGappedStartOffset(), actual.getGappedStartOffset());
        assertEquals(expected.getId(), expected.getGappedEndOffset(), actual.getGappedEndOffset());
        assertEquals(expected.getId(), expected.getGappedLength(), actual.getGappedLength());
        assertEquals(expected.getId(), expected.getReadInfo().getValidRange(), actual.getReadInfo().getValidRange());
        assertEquals(expected.getId(), expected.getNucleotideSequence().asList(), actual.getNucleotideSequence().asList());
        
    }
}
