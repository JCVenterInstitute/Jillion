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
 * Created on Feb 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.qual;

import org.jcvi.glyph.num.ByteGlyphFactory;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestLowestFlankingQualityValueStrategy {
    ByteGlyphFactory<PhredQuality> factory = new ByteGlyphFactory<PhredQuality>(){

        @Override
        protected PhredQuality createNewGlyph(Byte b) {
            return PhredQuality.valueOf(b);
        }
        
    };
    PhredQuality lower = factory.getGlyphFor((byte)5);
    PhredQuality higher = factory.getGlyphFor((byte)10);
    PhredQuality LOWEST = factory.getGlyphFor((byte)1);
    LowestFlankingQualityValueStrategy sut = new LowestFlankingQualityValueStrategy();
    
    
    @Test
    public void getQualityValueIfReadEndsWithGapShouldReturnLowestPossibleValue(){
        assertEquals(LOWEST, sut.getQualityValueIfReadEndsWithGap());
    }
    @Test
    public void getQualityValueIfReadStartsWithGapShouldReturnLowestPossibleValue(){
        assertEquals(LOWEST, sut.getQualityValueIfReadStartsWithGap());
    }
    @Test
    public void leftFlankIsLower(){
        assertEquals(lower, computeQualityValue(lower, higher));
    }
    @Test
    public void rightFlankIsLower(){
        assertEquals(lower, computeQualityValue(higher, lower));
    }
    @Test
    public void flanksSameShouldReturnThatValue(){
        assertEquals(higher, computeQualityValue(higher, higher));
        assertEquals(lower, computeQualityValue(lower, lower));
    }

    private PhredQuality computeQualityValue(PhredQuality leftFlank, PhredQuality rightFlank) {
        return sut.computeQualityValueForGap(1, 0, leftFlank, rightFlank);
    }
}
