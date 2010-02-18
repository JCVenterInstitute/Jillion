/*
 * Created on Feb 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;
import static org.junit.Assert.*;

import org.jcvi.Range;
import org.jcvi.TestUtil;
import org.jcvi.glyph.qualClass.QualityClass;
import org.junit.Test;
public class TestQualityClassRegion {

    Range range = Range.buildRange(5, 20);
    QualityClass qualityClass = QualityClass.valueOf((byte)20);
    QualityClass differentQualityClass = QualityClass.valueOf((byte)5);
    QualityClassRegion sut = new QualityClassRegion(qualityClass, range);
    
    @Test
    public void constructor(){
        assertEquals(qualityClass, sut.getQualityClass());
        assertEquals(range.getStart(), sut.getStart());
        assertEquals(range.getEnd(), sut.getEnd());
        assertEquals(range.size(), sut.getLength());
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
                Range.buildRange(10, 20));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRange);
    }
    
    @Test
    public void testToString(){
        String expected = range.toString()+" = quality class value "+qualityClass.getValue();
        assertEquals(expected, sut.toString());
    }
}
