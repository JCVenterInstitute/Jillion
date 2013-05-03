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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.trace.chromat.BasicChromatogram;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestBasicChromatogram {
    
    ChannelGroup mockChannelGroup = createMock(ChannelGroup.class);
    PositionSequence mockPeaks= createMock(PositionSequence.class);
    NucleotideSequence basecalls = createMock(NucleotideSequence.class);
    QualitySequence qualities = createMock(QualitySequence.class);
    Map<String,String> expectedProperties;
    private static final String PROP_1_KEY = "a key";
    private static final String PROP_2_KEY = "a different key";

    BasicChromatogram sut;
    String id = "id";
    @Before
    public void setup(){
        expectedProperties = new HashMap<String, String>();
        expectedProperties.put(PROP_1_KEY, "a value");
        expectedProperties.put(PROP_2_KEY, "a different value");

        sut = new BasicChromatogram(id,basecalls, qualities,mockPeaks, mockChannelGroup,
                expectedProperties);
    }

    @Test
    public void fullConstructor(){
        assertEquals(basecalls, sut.getNucleotideSequence());
        assertEquals(mockPeaks, sut.getPositionSequence());
        assertEquals(mockChannelGroup, sut.getChannelGroup());
        assertEquals(expectedProperties, sut.getComments());
        assertEquals(qualities, sut.getQualitySequence());
    }

    @Test
    public void constructionWithEmptyProperties(){
        BasicChromatogram emptyProps = new BasicChromatogram( id,
                                                basecalls,
                                                qualities,
                                                mockPeaks,
                                                mockChannelGroup);

        assertEquals(basecalls, emptyProps.getNucleotideSequence());
        assertEquals(mockPeaks, emptyProps.getPositionSequence());
        assertEquals(mockChannelGroup, emptyProps.getChannelGroup());
        assertEquals(qualities, sut.getQualitySequence());
        assertEquals(new Properties(), emptyProps.getComments());
    }
    
    @Test
    public void nullBaseCallsShouldThrowIllegalArugmentException(){
        try{
            new BasicChromatogram( id,
                    (NucleotideSequence)null,
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
            new BasicChromatogram( id,
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
            new BasicChromatogram( id,
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
            new BasicChromatogram( id,
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
        assertEquals(basecalls, copy.getNucleotideSequence());
        assertEquals(mockPeaks, copy.getPositionSequence());
        assertEquals(mockChannelGroup, copy.getChannelGroup());
        assertEquals(expectedProperties, copy.getComments());
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
        NucleotideSequence differentBases = createMock(NucleotideSequence.class);
        BasicChromatogram nullBases = new BasicChromatogram( id,differentBases, qualities,mockPeaks, mockChannelGroup,
                                    expectedProperties);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullBases);
    }

    
    @Test
    public void notEqualsDifferentPeaks(){
        PositionSequence differentPeaks = createMock(PositionSequence.class);
        BasicChromatogram nullPeaks = new BasicChromatogram( id,basecalls, qualities,differentPeaks, mockChannelGroup,
                expectedProperties);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullPeaks);
    }

   
    @Test
    public void notEqualsDifferentChannelGroup(){
        ChannelGroup differentChannelGroup = createMock(ChannelGroup.class);
        BasicChromatogram differentChannels = new BasicChromatogram( id,basecalls, qualities,mockPeaks, differentChannelGroup,
                expectedProperties);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentChannels);
    }

   
    @Test
    public void notEqualsExtraProperties(){
        HashMap<String, String> differentProperties = new HashMap<String,String>(expectedProperties);
        differentProperties.put("extra key", "extra value");
        BasicChromatogram hasDifferentProperties = new BasicChromatogram( id,basecalls, qualities,mockPeaks, mockChannelGroup,
                                        differentProperties);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentProperties);
    }
    @Test
    public void notEqualsMissingProperties(){
        HashMap<String, String> differentProperties = new HashMap<String,String>(expectedProperties);
        
        differentProperties.remove(PROP_1_KEY);
        BasicChromatogram hasDifferentProperties = new BasicChromatogram( id,basecalls, qualities,mockPeaks, mockChannelGroup,
                                        differentProperties);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentProperties);
    }


}
