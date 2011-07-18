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
 * Created on Dec 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.util.List;
import java.util.Properties;

import org.jcvi.common.core.seq.Sequence;
import org.jcvi.common.core.seq.ShortGlyph;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.jcvi.common.core.seq.qual.QualitySequence;
import org.jcvi.common.core.seq.read.trace.sanger.phd.ArtificialPhd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdTag;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class TestBuildArtificialPhd {

    NucleotideSequence mockBasecalls;
    QualitySequence mockQualities;
    Properties mockProperties;
    List<PhdTag> mockTags;
    String id = "phdId";
    long lengthOfBases= 5;
    int numberOfPositionsForEachPeak = 13;
    
    @Before
    public void setup(){
        mockBasecalls = createMock(NucleotideSequence.class);
        mockQualities = createMock(QualitySequence.class); 
        mockProperties = createMock(Properties.class); 
        mockTags = createMock(List.class); 
    }
    
    @Test
    public void noPropertiesAndTagsConstructor(){
        expect(mockBasecalls.getLength()).andReturn(lengthOfBases);
        replay(mockBasecalls, mockQualities);
        Phd phd = new ArtificialPhd(id,mockBasecalls, mockQualities, numberOfPositionsForEachPeak);
        assertEquals(id, phd.getId());
        assertEquals(mockBasecalls, phd.getBasecalls());
        assertEquals(mockQualities, phd.getQualities());
        Sequence<ShortGlyph> actualPeaks = phd.getPeaks().getData();
        for(int i=0; i< lengthOfBases; i++){
            assertEquals(Short.valueOf((short)(i*numberOfPositionsForEachPeak + numberOfPositionsForEachPeak)), actualPeaks.get(i).getNumber());
        }
        assertCommentsAndTagsAreEmpty(phd);
        verify(mockBasecalls, mockQualities);
    }
    private void assertCommentsAndTagsAreEmpty(Phd phd){
        assertTrue(phd.getComments().isEmpty());
        assertTrue(phd.getTags().isEmpty());
    }
    
    @Test
    public void withProperties(){
        expect(mockBasecalls.getLength()).andReturn(lengthOfBases);
        replay(mockBasecalls, mockQualities, mockProperties, mockTags);
        Phd phd =new ArtificialPhd(id,mockBasecalls, mockQualities, mockProperties, mockTags,numberOfPositionsForEachPeak);
        assertEquals(id, phd.getId());
        assertEquals(mockBasecalls, phd.getBasecalls());
        assertEquals(mockQualities, phd.getQualities());
        Sequence<ShortGlyph> actualPeaks = phd.getPeaks().getData();
        for(int i=0; i< lengthOfBases; i++){
            assertEquals(Short.valueOf((short)(i*numberOfPositionsForEachPeak + numberOfPositionsForEachPeak)), actualPeaks.get(i).getNumber());
        }
        assertEquals(mockProperties, phd.getComments());
        assertEquals(mockTags, phd.getTags());
        verify(mockBasecalls, mockQualities,mockProperties, mockTags);
    }
}
