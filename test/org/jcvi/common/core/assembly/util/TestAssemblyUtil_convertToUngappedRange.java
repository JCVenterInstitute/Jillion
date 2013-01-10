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

package org.jcvi.common.core.assembly.util;

import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.Range;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAssemblyUtil_convertToUngappedRange {
    private final Range range = Range.of(3,5);
    @Test
    public void noGapsShouldReturnSameRange(){
        assertEquals(range, AssemblyUtil.toUngappedRange(
                new NucleotideSequenceBuilder("ACGTACGT").build(), range));
        
    }
    @Test
    public void downstreamGapsShouldReturnSameRange(){
        assertEquals(range, AssemblyUtil.toUngappedRange(
                new NucleotideSequenceBuilder("ACGTAC-GT").build(), range));
        
    }
    @Test
    public void upstreamGapShouldShiftRange(){
        assertEquals(new Range.Builder(range).shift(-1).build(), AssemblyUtil.toUngappedRange(
                new NucleotideSequenceBuilder("A-CGTACGT").build(), range));
    }
    
    @Test
    public void gapsInsideRangeShouldShiftEndCoordinate(){        
        Range ungappedRagne = Range.of(3,4);
        assertEquals(ungappedRagne, AssemblyUtil.toUngappedRange(
                new NucleotideSequenceBuilder("ACGT-ACGT").build(), range));
    }
    
    @Test(expected=NullPointerException.class)
    public void nullSequenceShouldThrowNPE(){
        AssemblyUtil.toUngappedRange(null, range);
    }
    @Test(expected=NullPointerException.class)
    public void nullRangeShouldThrowNPE(){
        AssemblyUtil.toUngappedRange(
                new NucleotideSequenceBuilder("ACGTACGT").build()
                , null);
    }
}
