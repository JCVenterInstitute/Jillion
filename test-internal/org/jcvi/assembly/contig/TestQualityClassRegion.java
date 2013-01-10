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
 * Created on Feb 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;
import static org.junit.Assert.*;

import org.jcvi.common.core.testUtil.TestUtil;
import org.jcvi.glyph.qualClass.QualityClass;
import org.jcvi.jillion.core.Range;
import org.junit.Test;
public class TestQualityClassRegion {

    Range range = Range.of(5, 20);
    QualityClass qualityClass = QualityClass.valueOf((byte)20);
    QualityClass differentQualityClass = QualityClass.valueOf((byte)5);
    QualityClassRegion sut = new QualityClassRegion(qualityClass, range);
    
    @Test
    public void constructor(){
        assertEquals(qualityClass, sut.getQualityClass());
        assertEquals(range, sut.asRange());
    }
    
    @Test
    public void nullQualityClassShouldthrowIllegalArgumentException(){
        try{
            new QualityClassRegion(null, range);
            fail("should throw illegal argumentException");
        }catch(IllegalArgumentException e){
            assertEquals("qualityClass can not be null",e.getMessage());
        }
    }
    @Test
    public void nullRangeShouldthrowIllegalArgumentException(){
        try{
            new QualityClassRegion(qualityClass, null);
            fail("should throw illegal argumentException");
        }catch(IllegalArgumentException e){
            assertEquals("range can not be null",e.getMessage());
        }
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameValues(){
        QualityClassRegion sameValues = new QualityClassRegion(qualityClass, range);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("Not a quality class region"));
    }
    
    @Test
    public void differentQualityClassShouldNotBeEqual(){
        QualityClassRegion hasDifferentQualityClass = new QualityClassRegion(differentQualityClass, range);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentQualityClass);
    }
    
    @Test
    public void differentRangeShouldNotBeEqual(){
        QualityClassRegion hasDifferentRange = new QualityClassRegion(differentQualityClass, 
                Range.of(10, 20));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRange);
    }
    
    @Test
    public void testToString(){
        String expected = range.toString()+" = quality class value "+qualityClass.getValue();
        assertEquals(expected, sut.toString());
    }
}
