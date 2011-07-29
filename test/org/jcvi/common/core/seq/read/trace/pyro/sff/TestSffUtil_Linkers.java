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

package org.jcvi.common.core.seq.read.trace.pyro.sff;

import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFUtil;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
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
        assertMatches(seq, SFFUtil.Linkers.FLX.getForwardSequence());
        assertMatches(seq, SFFUtil.Linkers.FLX.getReverseSequence());
    }
    @Test
    public void titanium(){
        assertMatches("TCGTATAACTTCGTATAATGTATGCTATACGAAGTTATTACG", SFFUtil.Linkers.TITANIUM.getForwardSequence());
        assertMatches("CGTAATAACTTCGTATAGCATACATTATACGAAGTTATACGA", SFFUtil.Linkers.TITANIUM.getReverseSequence());
        
    }
    
    private void assertMatches(String expected, NucleotideSequence actual){
        assertEquals(expected, Nucleotide.convertToString(actual.decode()));
    }
}
