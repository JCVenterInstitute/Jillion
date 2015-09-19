/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Dec 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.phd;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.jcvi.jillion.assembly.consed.phd.ArtificialPhd;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdReadTag;
import org.jcvi.jillion.assembly.consed.phd.PhdWholeReadItem;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
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
        PositionSequence actualPeaks = phd.getPeakSequence();
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
        PositionSequence actualPeaks = phd.getPeakSequence();
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
