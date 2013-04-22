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
 * Created on Dec 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.phd;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.sanger.PositionSequence;
import org.junit.Before;
import org.junit.Test;

public class TestBuildArtificialPhd {

    NucleotideSequence mockBasecalls;
    QualitySequence mockQualities;
    Map<String,String> mockProperties;
    List<PhdWholeReadItem> mockReadItems;
    List<PhdReadTag> mockReadTags;
    
    String id = "phdId";
    long lengthOfBases= 5;
    int numberOfPositionsForEachPeak = 13;
    
    @Before
    public void setup(){
        mockBasecalls = createMock(NucleotideSequence.class);
        mockQualities = createMock(QualitySequence.class); 
        mockProperties = createMock(Map.class); 
        mockReadItems = createMock(List.class); 
        mockReadTags = createMock(List.class);
    }
    
    @Test
    public void noPropertiesAndTagsConstructor(){
        expect(mockBasecalls.getLength()).andReturn(lengthOfBases);
        replay(mockBasecalls, mockQualities);
        Phd phd = new ArtificialPhd(id,mockBasecalls, mockQualities, numberOfPositionsForEachPeak);
        assertEquals(id, phd.getId());
        assertEquals(mockBasecalls, phd.getNucleotideSequence());
        assertEquals(mockQualities, phd.getQualitySequence());
        PositionSequence actualPeaks = phd.getPositionSequence();
        for(int i=0; i< lengthOfBases; i++){
            assertEquals(
            		i*numberOfPositionsForEachPeak + numberOfPositionsForEachPeak,
            		actualPeaks.get(i).getValue());
        }
        assertCommentsAndTagsAreEmpty(phd);
        verify(mockBasecalls, mockQualities);
    }
    private void assertCommentsAndTagsAreEmpty(Phd phd){
        assertTrue(phd.getComments().isEmpty());
        assertTrue(phd.getWholeReadItems().isEmpty());
        assertTrue(phd.getReadTags().isEmpty());
    }
    
    @Test
    public void withProperties(){
        expect(mockBasecalls.getLength()).andReturn(lengthOfBases);
        replay(mockBasecalls, mockQualities, mockProperties, mockReadItems);
        Phd phd =new ArtificialPhd(id,mockBasecalls, mockQualities, mockProperties, mockReadItems,mockReadTags, numberOfPositionsForEachPeak);
        assertEquals(id, phd.getId());
        assertEquals(mockBasecalls, phd.getNucleotideSequence());
        assertEquals(mockQualities, phd.getQualitySequence());
        PositionSequence actualPeaks = phd.getPositionSequence();
        for(int i=0; i< lengthOfBases; i++){
            assertEquals(i*numberOfPositionsForEachPeak + numberOfPositionsForEachPeak
            		, actualPeaks.get(i).getValue());
        }
        assertEquals(mockProperties, phd.getComments());
        assertEquals(mockReadItems, phd.getWholeReadItems());
        assertEquals(mockReadTags, phd.getReadTags());
        verify(mockBasecalls, mockQualities,mockProperties, mockReadItems);
    }
}
