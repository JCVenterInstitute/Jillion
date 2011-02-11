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
 * Created on Nov 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.testUtil.TestUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFFlowgram;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestSFFFlowgram {

    Range qualitiesClip = Range.buildRange(10,90);
    Range adapterClip= Range.buildRange(5,95);
    QualityEncodedGlyphs confidence = createMock(QualityEncodedGlyphs.class);
    List<Short> values = convertIntoList(new short[]{202, 310,1,232,7});
    NucleotideEncodedGlyphs basecalls = createMock(NucleotideEncodedGlyphs.class);
    String id = "readId";
    SFFFlowgram sut;
    @Before
    public void setup(){
        expect(basecalls.decode()).andStubReturn(NucleotideGlyph.getGlyphsFor("ACGT"));
        expect(confidence.decode()).andStubReturn(PhredQuality.valueOf(new byte[]{20,15,30,15}));
        
        replay(basecalls,confidence);
        sut = new SFFFlowgram(id,basecalls,confidence,values,qualitiesClip, adapterClip);
        
    }

    private static List<Short> convertIntoList(short[] values) {
        List<Short> valueList = new ArrayList<Short>();
        for(short s: values){
            valueList.add(s);
        }
        return valueList;
    }
    
    @Test
    public void constructor(){
        assertEquals(id, sut.getId());
        assertEquals(basecalls, sut.getBasecalls());
        assertEquals(confidence, sut.getQualities());
        assertEquals(qualitiesClip, sut.getQualitiesClip());
        assertEquals(adapterClip, sut.getAdapterClip());
        assertEquals(values.size(), sut.getSize());
        for(int i=0; i< values.size(); i++){
            assertEquals(SFFUtil.convertFlowgramValue(values.get(i)), 
                            sut.getValueAt(i),0);
        }
    }
    @Test
    public void nullIdShouldthrowNullPointerException(){
        try{
            new SFFFlowgram(null,basecalls,confidence,values,qualitiesClip, adapterClip);
            fail("should throw nullPointerException when id is null");
        }
        catch(NullPointerException expected){
            assertEquals("id can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullBasecallsShouldthrowNullPointerException(){
        try{
            new SFFFlowgram(id,null,confidence,values,qualitiesClip, adapterClip);
            fail("should throw nullPointerException when basecalls is null");
        }
        catch(NullPointerException expected){
            assertEquals("basecalls can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullQualitiesShouldthrowNullPointerException(){
        try{
            new SFFFlowgram(id,basecalls,null,values,qualitiesClip, adapterClip);
            fail("should throw nullPointerException when qualities is null");
        }
        catch(NullPointerException expected){
            assertEquals("qualities can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullValuesShouldthrowNullPointerException(){
        try{
            new SFFFlowgram(id,basecalls,confidence,null,qualitiesClip, adapterClip);
            fail("should throw nullPointerException when values is null");
        }
        catch(NullPointerException expected){
            assertEquals("values can not be null", expected.getMessage());
        }
    }
    @Test
    public void emptyValuesShouldthrowIllegalArgumentException(){
        try{
            new SFFFlowgram(id,basecalls,confidence,Collections.<Short>emptyList(),qualitiesClip, adapterClip);
            fail("should throw IllegalArgumentException when values is empty");
        }
        catch(IllegalArgumentException expected){
            assertEquals("values can not be empty", expected.getMessage());
        }
    }
    @Test
    public void nullQualitiesClipShouldthrowNullPointerException(){
        try{
            new SFFFlowgram(id,basecalls,confidence,values,null, adapterClip);
            fail("should throw nullPointerException when qualitiesClip is null");
        }
        catch(NullPointerException expected){
            assertEquals("qualitiesClip can not be null", expected.getMessage());
        }
    }
    @Test
    public void nullAdapterClipShouldthrowNullPointerException(){
        try{
            new SFFFlowgram(id,basecalls,confidence,values,qualitiesClip, null);
            fail("should throw nullPointerException when adapterClip is null");
        }
        catch(NullPointerException expected){
            assertEquals("adapterClip can not be null", expected.getMessage());
        }
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a SFFFlowgram"));
    }
    @Test
    public void equalsSameData(){
        SFFFlowgram sameData = new SFFFlowgram(id,basecalls,confidence,values,qualitiesClip, adapterClip);
        TestUtil.assertEqualAndHashcodeSame(sut, sameData);
    }
    @Test
    public void notEqualsDifferentValues(){
        SFFFlowgram differentValues = new SFFFlowgram(id,basecalls,confidence,
                convertIntoList(new short[]{1,2,3,4,5,6,7}),
                    qualitiesClip, adapterClip);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsValues(){
        SFFFlowgram differentValues = new SFFFlowgram(id,basecalls,confidence,
                convertIntoList(new short[]{1,2,3,4,5,6,7}),
                    qualitiesClip, adapterClip);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
}
