/*
 * Created on Feb 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.qual;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.fasta.QualityFastaRecord;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
public class TestAbstractQualityValueStrategy {
    private static final int LAST_INDEX = 10;
    protected static final int LENGTH = LAST_INDEX+1;
    PlacedRead placedRead;
    int gappedIndex = 5;
    QualityFastaRecord<EncodedGlyphs<PhredQuality>> qualityFasta;
    
    AbstractQualityValueStrategy sut;
    NucleotideEncodedGlyphs encodedGlyphs;
    Range validRange = Range.buildRange(0, LAST_INDEX);
    EncodedGlyphs<PhredQuality> qualities;
    PhredQuality leftQuality = PhredQuality.valueOf(20);
    PhredQuality rightQuality = PhredQuality.valueOf(30);
    PhredQuality expectedQuality = PhredQuality.valueOf(40);
    @Before
    public void setup() throws SecurityException, NoSuchMethodException{
        placedRead = createMock(PlacedRead.class);
        qualityFasta = createMock(QualityFastaRecord.class);
        encodedGlyphs = createMock(NucleotideEncodedGlyphs.class);
        qualities = createMock(EncodedGlyphs.class);
        sut = createMock(AbstractQualityValueStrategy.class,
                AbstractQualityValueStrategy.class.getDeclaredMethod("getQualityValueIfReadStartsWithGap",(Class[])null),
                AbstractQualityValueStrategy.class.getDeclaredMethod("getQualityValueIfReadEndsWithGap",(Class[])null),
                AbstractQualityValueStrategy.class.getDeclaredMethod("computeQualityValueForGap",
                                        new Class[]{int.class, int.class,PhredQuality.class, PhredQuality.class}));
        expect(placedRead.getEncodedGlyphs()).andStubReturn(encodedGlyphs);
        expect(placedRead.getValidRange()).andStubReturn(validRange);
        expect(placedRead.getLength()).andStubReturn((long)LENGTH);
        expect(qualityFasta.getValues()).andStubReturn(qualities);
        expect(placedRead.getSequenceDirection()).andReturn(getSequenceDirection()).anyTimes();
        expect(qualities.getLength()).andStubReturn((LENGTH));
    }

    protected SequenceDirection getSequenceDirection() {
        return SequenceDirection.FORWARD;
    }
    
    protected int complimentIfNeeded(int fullRangeIndex){
        return fullRangeIndex;
    }
    
    @Test
    public void notAGapShouldReturnQualityAtIndex(){        
        expectANonGapAt(gappedIndex, expectedQuality);
        replay(sut,placedRead, qualityFasta, qualities,encodedGlyphs);
        assertEquals(expectedQuality,sut.getQualityFor(placedRead, qualities, gappedIndex));
        verify(sut,placedRead, qualityFasta,qualities,encodedGlyphs);
    }
    
    @Test
    public void oneGap(){        
        expectAGapAt(gappedIndex);
        expectANonGapAt(gappedIndex-1, leftQuality);
        expectANonGapAt(gappedIndex+1, rightQuality);
        expect(sut.computeQualityValueForGap(1, 0, leftQuality, rightQuality)).andReturn(expectedQuality);
        
        replay(sut,placedRead, qualityFasta, qualities,encodedGlyphs);
        assertEquals(expectedQuality,sut.getQualityFor(placedRead, qualities, gappedIndex));
        verify(sut,placedRead, qualityFasta,qualities,encodedGlyphs);
    }
    @Test
    public void threeGapsIndexIsInMiddle(){        
        expectAGapAt(gappedIndex);
        expectAGapAt(gappedIndex-1);
        expectANonGapAt(gappedIndex-2, leftQuality);
        expectAGapAt(gappedIndex+1);
        expectANonGapAt(gappedIndex+2, rightQuality);
        expect(sut.computeQualityValueForGap(3, 1, leftQuality, rightQuality)).andReturn(expectedQuality);
        
        replay(sut,placedRead, qualityFasta, qualities,encodedGlyphs);
        assertEquals(expectedQuality,sut.getQualityFor(placedRead, qualities, gappedIndex));
        verify(sut,placedRead, qualityFasta,qualities,encodedGlyphs);
    }

    @Test
    public void gapAtStartOfRead(){
        expectAGapAt(0);
        expect(encodedGlyphs.isGap(1)).andReturn(false);
        expect(sut.getQualityValueIfReadStartsWithGap()).andReturn(expectedQuality);
        replay(sut,placedRead, qualityFasta, qualities,encodedGlyphs);
        assertEquals(expectedQuality,sut.getQualityFor(placedRead, qualities, 0));
        verify(sut,placedRead, qualityFasta,qualities,encodedGlyphs);
    }
    @Test
    public void manyGapsAtStartOfRead(){
        expectAGapAt(0);
        expectAGapAt(1);
        expect(encodedGlyphs.isGap(2)).andReturn(false);
        expect(sut.getQualityValueIfReadStartsWithGap()).andReturn(expectedQuality);
        replay(sut,placedRead, qualityFasta, qualities,encodedGlyphs);
        assertEquals(expectedQuality,sut.getQualityFor(placedRead, qualities, 1));
        verify(sut,placedRead, qualityFasta,qualities,encodedGlyphs);
    }
    
    @Test
    public void gapAtEndOfRead(){
        expectAGapAt(LAST_INDEX);
        expect(encodedGlyphs.isGap(LAST_INDEX-1)).andReturn(false);
        expect(sut.getQualityValueIfReadEndsWithGap()).andReturn(expectedQuality);
        replay(sut,placedRead, qualityFasta, qualities,encodedGlyphs);
        assertEquals(expectedQuality,sut.getQualityFor(placedRead, qualities, LAST_INDEX));
        verify(sut,placedRead, qualityFasta,qualities,encodedGlyphs);
    }
    @Test
    public void manyGapsAtEndOfRead(){
        expectAGapAt(LAST_INDEX);
        expectAGapAt(LAST_INDEX-1);
        expect(encodedGlyphs.isGap(LAST_INDEX-2)).andReturn(false).times(2);
        expect(sut.getQualityValueIfReadEndsWithGap()).andReturn(expectedQuality).times(2);
        replay(sut,placedRead, qualityFasta, qualities,encodedGlyphs);
        assertEquals(expectedQuality,sut.getQualityFor(placedRead, qualities, LAST_INDEX-1));
        assertEquals(expectedQuality,sut.getQualityFor(placedRead, qualities, LAST_INDEX));
        verify(sut,placedRead, qualityFasta,qualities,encodedGlyphs);
    }
    private void expectAGapAt(int currentIndex) {
        expect(encodedGlyphs.isGap(currentIndex)).andReturn(true).atLeastOnce();
    }

    private void expectANonGapAt(int currentIndex, PhredQuality leftQuality) {
        expect(encodedGlyphs.isGap(currentIndex)).andReturn(false).atLeastOnce();
        expectGetQualityFor(encodedGlyphs, qualities, currentIndex, leftQuality);
    }
    private void expectGetQualityFor(NucleotideEncodedGlyphs glyphs, EncodedGlyphs<PhredQuality> qualities,int index, PhredQuality qualityToReturn){
        expect(glyphs.convertGappedValidRangeIndexToUngappedValidRangeIndex(index)).andReturn(index);
        final int convertedindex = complimentIfNeeded(index);
        expect(qualities.get(convertedindex)).andReturn(qualityToReturn);
        
    }
}
