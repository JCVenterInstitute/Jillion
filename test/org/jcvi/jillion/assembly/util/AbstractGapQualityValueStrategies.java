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
package org.jcvi.jillion.assembly.util;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMockSupport;
import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractGapQualityValueStrategies extends EasyMockSupport{

    GapQualityValueStrategy sut;
    private AssembledRead placedRead;
    PhredQuality expectedQuality = PhredQuality.valueOf(42);
    @Before
    public void setup(){
        sut= getGapQualityValueStrategies();
        placedRead= createMock(AssembledRead.class);
    }
    
    protected abstract GapQualityValueStrategy getGapQualityValueStrategies();
    
    @Test(expected = NullPointerException.class)
    public void getGappedValidRangeQualSequenceByReadWithNullQualitiesShouldThrowNPE(){
        sut.getGappedValidRangeQualitySequenceFor(placedRead, null);
    }
    @Test(expected = NullPointerException.class)
    public void getGappedValidRangeQualSequenceByReadWithNullReadShouldThrowNPE(){
        sut.getGappedValidRangeQualitySequenceFor(null, createMock(QualitySequence.class));
    }
    @Test
    public void testGappedValidRangeQualityForwardSequence(){
    	QualitySequenceBuilder fullLengthQualities = new QualitySequenceBuilder();
    	for(int i =0; i<15; i++){
    		fullLengthQualities.append(i+1);
    	}
    	NucleotideSequence seq = new NucleotideSequenceBuilder("AA-A-AAAA--AA-A")
    									.build();
    	
    	ReferenceMappedNucleotideSequence readSeq = new NucleotideSequenceBuilder(seq)
    													.setReferenceHint(seq, 0)
    													.buildReferenceEncodedNucleotideSequence();
    	
    	expect(placedRead.getNucleotideSequence()).andStubReturn(readSeq);
    	Range validRange = Range.of(2,11);
		expect(placedRead.getReadInfo()).andStubReturn(new ReadInfo(validRange, 15));
    	expect(placedRead.getDirection()).andStubReturn(Direction.FORWARD);
    	expect(placedRead.getGappedLength()).andStubReturn(seq.getLength());
    	replayAll();
    	QualitySequence fullLengthUngappedQualities = fullLengthQualities.build();
    	
    	
    	//clr =     |                    |
    	//raw = 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
        //          A A -A- A A A A--AA-A
    	
    	
    	QualitySequenceBuilder expectedBuilder = new QualitySequenceBuilder(fullLengthUngappedQualities)
    													.trim(validRange);
    	
    	expectedBuilder.insert(2, sut.computeQualityValueForGap(PhredQuality.valueOf(4), PhredQuality.valueOf(5)));
    	expectedBuilder.insert(4, sut.computeQualityValueForGap(PhredQuality.valueOf(5), PhredQuality.valueOf(6)));
    	
    	expectedBuilder.insert(9, sut.computeQualityValueForGap(PhredQuality.valueOf(9), PhredQuality.valueOf(10)));
    	expectedBuilder.insert(10, sut.computeQualityValueForGap(PhredQuality.valueOf(9), PhredQuality.valueOf(10)));
    	
    	expectedBuilder.insert(13, sut.computeQualityValueForGap(PhredQuality.valueOf(11), PhredQuality.valueOf(12)));
    	
    	QualitySequence expectedValidRangeGappedQualities = expectedBuilder.build();
    	QualitySequence validRangeGappedQualities = sut.getGappedValidRangeQualitySequenceFor(placedRead, fullLengthUngappedQualities);
    	
    	assertEquals(expectedValidRangeGappedQualities, validRangeGappedQualities);
    	
    }
    
    @Test
    public void testGappedValidRangeQualityReverseSequence(){
    	QualitySequenceBuilder fullLengthQualities = new QualitySequenceBuilder();
    	for(int i =0; i<15; i++){
    		fullLengthQualities.append(i+1);
    	}
    	NucleotideSequence seq = new NucleotideSequenceBuilder("AA-A-AAAA--AA-A")
    									.build();
    	
    	ReferenceMappedNucleotideSequence readSeq = new NucleotideSequenceBuilder(seq)
    													.setReferenceHint(seq, 0)
    													.buildReferenceEncodedNucleotideSequence();
    	
    	expect(placedRead.getNucleotideSequence()).andStubReturn(readSeq);
    	Range validRange = Range.of(2,11);
		expect(placedRead.getReadInfo()).andStubReturn(new ReadInfo(validRange, 15));
    	expect(placedRead.getDirection()).andStubReturn(Direction.REVERSE);
    	expect(placedRead.getGappedLength()).andStubReturn(seq.getLength());
    	replayAll();
    	QualitySequence fullLengthUngappedQualities = fullLengthQualities.build();
    	
    	//clr =     |                    |
    	//raw = 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15

    	//clr =          |                    |
    	//rev = 15 14 13 12 11 10 9 8 7 6 5 4 3 2 1
    	//             A A -A- A A A A--AA-A
    	
    	QualitySequenceBuilder expectedBuilder = new QualitySequenceBuilder(
    																new byte[]{12,11,10,9,8,7,6,5,4,3});
    	
    	expectedBuilder.insert(2, sut.computeQualityValueForGap(PhredQuality.valueOf(12), PhredQuality.valueOf(11)));
    	expectedBuilder.insert(4, sut.computeQualityValueForGap(PhredQuality.valueOf(11), PhredQuality.valueOf(10)));
    	
    	expectedBuilder.insert(9, sut.computeQualityValueForGap(PhredQuality.valueOf(7), PhredQuality.valueOf(6)));
    	expectedBuilder.insert(10, sut.computeQualityValueForGap(PhredQuality.valueOf(7), PhredQuality.valueOf(6)));
    	
    	expectedBuilder.insert(13, sut.computeQualityValueForGap(PhredQuality.valueOf(5), PhredQuality.valueOf(4)));
    	
    	QualitySequence expectedValidRangeGappedQualities = expectedBuilder.build();
    	QualitySequence validRangeGappedQualities = sut.getGappedValidRangeQualitySequenceFor(placedRead, fullLengthUngappedQualities);
    	
    	assertEquals(expectedValidRangeGappedQualities, validRangeGappedQualities);
    	
    }
    
}
