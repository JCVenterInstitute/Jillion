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
