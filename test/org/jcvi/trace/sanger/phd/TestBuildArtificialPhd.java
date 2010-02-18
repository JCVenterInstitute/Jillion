/*
 * Created on Dec 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.util.List;
import java.util.Properties;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

public class TestBuildArtificialPhd {

    NucleotideEncodedGlyphs mockBasecalls;
    EncodedGlyphs<PhredQuality> mockQualities;
    Properties mockProperties;
    List<PhdTag> mockTags;
    
    long lengthOfBases= 5;
    int numberOfPositionsForEachPeak = 13;
    
    @Before
    public void setup(){
        mockBasecalls = createMock(NucleotideEncodedGlyphs.class);
        mockQualities = createMock(EncodedGlyphs.class); 
        mockProperties = createMock(Properties.class); 
        mockTags = createMock(List.class); 
    }
    
    @Test
    public void noPropertiesAndTagsConstructor(){
        expect(mockBasecalls.getLength()).andReturn(lengthOfBases);
        replay(mockBasecalls, mockQualities);
        Phd phd = new ArtificialPhd(mockBasecalls, mockQualities, numberOfPositionsForEachPeak);
        
        assertEquals(mockBasecalls, phd.getBasecalls());
        assertEquals(mockQualities, phd.getQualities());
        EncodedGlyphs<ShortGlyph> actualPeaks = phd.getPeaks().getData();
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
        Phd phd =new ArtificialPhd(mockBasecalls, mockQualities, mockProperties, mockTags,numberOfPositionsForEachPeak);
        
        assertEquals(mockBasecalls, phd.getBasecalls());
        assertEquals(mockQualities, phd.getQualities());
        EncodedGlyphs<ShortGlyph> actualPeaks = phd.getPeaks().getData();
        for(int i=0; i< lengthOfBases; i++){
            assertEquals(Short.valueOf((short)(i*numberOfPositionsForEachPeak + numberOfPositionsForEachPeak)), actualPeaks.get(i).getNumber());
        }
        assertEquals(mockProperties, phd.getComments());
        assertEquals(mockTags, phd.getTags());
        verify(mockBasecalls, mockQualities,mockProperties, mockTags);
    }
}
