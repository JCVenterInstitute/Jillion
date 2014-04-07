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
/*
 * Created on Mar 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class AssemblyTestUtil {

    public static void assertPlacedReadCorrect(AssembledRead expected,
            AssembledRead actual) {
        String id = expected.getId();
		assertEquals("ids", id, actual.getId());
        assertEquals(id + " startOffset", expected.getGappedStartOffset(), actual.getGappedStartOffset());
        assertEquals(id + " gapped length", expected.getGappedLength(), actual.getGappedLength());
        assertEquals(id, expected.getDirection(), actual.getDirection());
        assertEquals(id, expected.getReadInfo(), actual.getReadInfo());
        final NucleotideSequence expectedEncodedGlyphs = expected.getNucleotideSequence();
        final NucleotideSequence actualEncodedGlyphs = actual.getNucleotideSequence();
        assertEquals(id, expectedEncodedGlyphs, actualEncodedGlyphs);
        
        
    }
}
