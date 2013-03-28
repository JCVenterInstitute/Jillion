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
 * Created on Mar 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class AssemblyTestUtil {

    public static void assertPlacedReadCorrect(AssembledRead expected,
            AssembledRead actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getGappedStartOffset(), actual.getGappedStartOffset());
        assertEquals(expected.getGappedLength(), actual.getGappedLength());
        assertEquals(expected.getDirection(), actual.getDirection());
        assertEquals(expected.getReadInfo(), actual.getReadInfo());
        final NucleotideSequence expectedEncodedGlyphs = expected.getNucleotideSequence();
        final NucleotideSequence actualEncodedGlyphs = actual.getNucleotideSequence();
        assertEquals(expectedEncodedGlyphs, actualEncodedGlyphs);
        
        
    }
}
