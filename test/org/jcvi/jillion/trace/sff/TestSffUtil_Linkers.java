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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.sff;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.sff.SffUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestSffUtil_Linkers {

    @Test
    public void flx(){
        String seq = "GTTGGAACCGAAAGGGTTTGAATTCAAACCCTTTCGGTTCCAAC";
        assertMatches(seq, SffUtil.Linkers.FLX.getForwardSequence());
        assertMatches(seq, SffUtil.Linkers.FLX.getReverseSequence());
    }
    @Test
    public void titanium(){
        assertMatches("TCGTATAACTTCGTATAATGTATGCTATACGAAGTTATTACG", SffUtil.Linkers.TITANIUM.getForwardSequence());
        assertMatches("CGTAATAACTTCGTATAGCATACATTATACGAAGTTATACGA", SffUtil.Linkers.TITANIUM.getReverseSequence());
        
    }
    
    private void assertMatches(String expected, NucleotideSequence actual){
        assertEquals(expected, actual.toString());
    }
}
