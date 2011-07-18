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
/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import org.jcvi.Range;
import org.jcvi.common.core.seq.read.trace.pyro.sff.NewblerSuffixNameConverter;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestNewblerSuffixNameConverter {

    
    private static final String ORIGINAL_NAME = "C3U5GNB01C40I3";
   
    
    
    
    @Test
    public void getSffNameWithRange(){
        String suffixed = ORIGINAL_NAME+".1-60";
        assertEquals(ORIGINAL_NAME, NewblerSuffixNameConverter.getUnSuffixedNameFrom(suffixed));
    }
    @Test
    public void getSffNameWithFrom(){
        String suffixed = ORIGINAL_NAME+".1-60.fm2";
        assertEquals(ORIGINAL_NAME, NewblerSuffixNameConverter.getUnSuffixedNameFrom(suffixed));
    }
    @Test
    public void getSffNameWithTo(){
        String suffixed = ORIGINAL_NAME+".1-60.to45";
        assertEquals(ORIGINAL_NAME, NewblerSuffixNameConverter.getUnSuffixedNameFrom(suffixed));
    }
    @Test
    public void getSffNameWithPairedEnds(){
        String suffixedLeft = ORIGINAL_NAME+"_left";
        String suffixedRight = ORIGINAL_NAME+"_right";
        assertEquals(ORIGINAL_NAME, NewblerSuffixNameConverter.getUnSuffixedNameFrom(suffixedLeft));
        assertEquals(ORIGINAL_NAME, NewblerSuffixNameConverter.getUnSuffixedNameFrom(suffixedRight));
    }
    
    @Test
    public void getSffNameWithPairedEndsAlignToDifferentContigs(){
        String suffixedLeft = ORIGINAL_NAME+"_left.pr87";
        String suffixedRight = ORIGINAL_NAME+"_right.pr36";
        assertEquals(ORIGINAL_NAME, NewblerSuffixNameConverter.getUnSuffixedNameFrom(suffixedLeft));
        assertEquals(ORIGINAL_NAME, NewblerSuffixNameConverter.getUnSuffixedNameFrom(suffixedRight));
    }
    
    @Test
    public void getSffNameWithTrimmedPairedEndsAlignToDifferentContigs(){
        String suffixedLeft = ORIGINAL_NAME+"_left.1-60.pr87";
        String suffixedRight = ORIGINAL_NAME+"_right.61-248.pr36";
        assertEquals(ORIGINAL_NAME, NewblerSuffixNameConverter.getUnSuffixedNameFrom(suffixedLeft));
        assertEquals(ORIGINAL_NAME, NewblerSuffixNameConverter.getUnSuffixedNameFrom(suffixedRight));
    }
    
    @Test
    public void getRangeNoRangeShouldReturnNull(){
        assertNull(NewblerSuffixNameConverter.getSuffixedRangeFrom(ORIGINAL_NAME));
    }
    
    @Test
    public void getRangeShouldReturnZeroBased(){
        Range expectedRange = Range.buildRange(0, 10);
        assertEquals(expectedRange, NewblerSuffixNameConverter.getSuffixedRangeFrom(ORIGINAL_NAME+".1-11"));
    }
    @Test
    public void getReversedRangeShouldSwapLeftAndRight(){
        Range expectedRange = Range.buildRange(23, 543);
        assertEquals(expectedRange, NewblerSuffixNameConverter.getSuffixedRangeFrom(ORIGINAL_NAME+".544-24"));
    }
    
    
}
