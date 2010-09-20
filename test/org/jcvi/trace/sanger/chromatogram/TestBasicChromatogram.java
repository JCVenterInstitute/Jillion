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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Peaks;
import org.jcvi.testUtil.TestUtil;
import org.jcvi.trace.sanger.chromatogram.BasicChromatogram;
import org.jcvi.trace.sanger.chromatogram.ChannelGroup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestBasicChromatogram {
    
    ChannelGroup mockChannelGroup = createMock(ChannelGroup.class);
    Peaks mockPeaks= createMock(Peaks.class);
    NucleotideEncodedGlyphs basecalls = createMock(NucleotideEncodedGlyphs.class);
    EncodedGlyphs<PhredQuality> qualities = createMock(EncodedGlyphs.class);
    Map<String,String> expectedProperties;
    private static final String PROP_1_KEY = "a key";
    private static final String PROP_2_KEY = "a different key";

    BasicChromatogram sut;
    @Before
    public void setup(){
        expectedProperties = new HashMap<String, String>();
        expectedProperties.put(PROP_1_KEY, "a value");
        expectedProperties.put(PROP_2_KEY, "a different value");

        sut = new BasicChromatogram(basecalls, qualities,mockPeaks, mockChannelGroup,
                expectedProperties);
    }

    @Test
    public void fullConstructor(){
        assertEquals(basecalls, sut.getBasecalls());
        assertEquals(mockPeaks, sut.getPeaks());
        assertEquals(mockChannelGroup, sut.getChannelGroup());
        assertEquals(expectedProperties, sut.getProperties());
        assertEquals(qualities, sut.getQualities());
    }

    @Test
    public void constructionWithEmptyProperties(){
        BasicChromatogram emptyProps = new BasicChromatogram(
                                                basecalls,
                                                qualities,
                                                mockPeaks,
                                                mockChannelGroup);

        assertEquals(basecalls, emptyProps.getBasecalls());
        assertEquals(mockPeaks, emptyProps.getPeaks());
        assertEquals(mockChannelGroup, emptyProps.getChannelGroup());
        assertEquals(qualities, sut.getQualities());
        assertEquals(new Properties(), emptyProps.getProperties());
    }
    
    @Test
    public void nullBaseCallsShouldThrowIllegalArugmentException(){
        try{
            new BasicChromatogram(
                    (NucleotideEncodedGlyphs)null,
                    qualities,
                    mockPeaks,
                    mockChannelGroup,
                    expectedProperties);
            fail("should throw illegalArgumentException when a parameter is null");
        }catch(IllegalArgumentException e){
            assertEquals("null parameter", e.getMessage());
        }
    }
    @Test
    public void nullPeaksShouldThrowIllegalArugmentException(){
        try{
            new BasicChromatogram(
                    basecalls,
                    qualities,
                    null,
                    mockChannelGroup,
                    expectedProperties);
            fail("should throw illegalArgumentException when a parameter is null");
        }catch(IllegalArgumentException e){
            assertEquals("null parameter", e.getMessage());
        }
    }
    @Test
    public void nullChannelGroupShouldThrowIllegalArugmentException(){
        try{
            new BasicChromatogram(
                    basecalls,
                    qualities,
                    mockPeaks,
                    
                    null,
                    expectedProperties);
            fail("should throw illegalArgumentException when a parameter is null");
        }catch(IllegalArgumentException e){
            assertEquals("null parameter", e.getMessage());
        }
    }
    @Test
    public void nullPropertiesShouldThrowIllegalArugmentException(){
        try{
            new BasicChromatogram(
                    basecalls,
                    qualities,
                    mockPeaks,
                    mockChannelGroup,
                    null);
            fail("should throw illegalArgumentException when a parameter is null");
        }catch(IllegalArgumentException e){
            assertEquals("null parameter", e.getMessage());
        }
    }

    @Test
    public void copyConstructor(){
        BasicChromatogram copy = new BasicChromatogram(sut);
        assertEquals(basecalls, copy.getBasecalls());
        assertEquals(mockPeaks, copy.getPeaks());
        assertEquals(mockChannelGroup, copy.getChannelGroup());
        assertEquals(expectedProperties, copy.getProperties());
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
        assertFalse(sut.equals("not a chromatogram"));
    }

    @Test
    public void equalsSameValues(){
        BasicChromatogram copy = new BasicChromatogram(sut);
        TestUtil.assertEqualAndHashcodeSame(sut, copy);
    }
   
    @Test
    public void notEqualsDifferentBasecalls(){
        NucleotideEncodedGlyphs differentBases = createMock(NucleotideEncodedGlyphs.class);
        BasicChromatogram nullBases = new BasicChromatogram(differentBases, qualities,mockPeaks, mockChannelGroup,
                                    expectedProperties);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullBases);
    }

    
    @Test
    public void notEqualsDifferentPeaks(){
        Peaks differentPeaks = createMock(Peaks.class);
        BasicChromatogram nullPeaks = new BasicChromatogram(basecalls, qualities,differentPeaks, mockChannelGroup,
                expectedProperties);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullPeaks);
    }

   
    @Test
    public void notEqualsDifferentChannelGroup(){
        ChannelGroup differentChannelGroup = createMock(ChannelGroup.class);
        BasicChromatogram differentChannels = new BasicChromatogram(basecalls, qualities,mockPeaks, differentChannelGroup,
                expectedProperties);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentChannels);
    }

   
    @Test
    public void notEqualsExtraProperties(){
        HashMap<String, String> differentProperties = new HashMap<String,String>(expectedProperties);
        differentProperties.put("extra key", "extra value");
        BasicChromatogram hasDifferentProperties = new BasicChromatogram(basecalls, qualities,mockPeaks, mockChannelGroup,
                                        differentProperties);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentProperties);
    }
    @Test
    public void notEqualsMissingProperties(){
        HashMap<String, String> differentProperties = new HashMap<String,String>(expectedProperties);
        
        differentProperties.remove(PROP_1_KEY);
        BasicChromatogram hasDifferentProperties = new BasicChromatogram(basecalls, qualities,mockPeaks, mockChannelGroup,
                                        differentProperties);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentProperties);
    }


}
