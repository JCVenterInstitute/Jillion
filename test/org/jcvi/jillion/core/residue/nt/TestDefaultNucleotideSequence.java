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
package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultNucleotideSequence {

    private String gappedBasecalls = "ACGTACGT-ACGTACG-T";
    
    NucleotideSequence sut = new NucleotideSequenceBuilder(gappedBasecalls)
    								.build();
    
    private List<Nucleotide> asList(NucleotideSequence seq){
    	List<Nucleotide> list = new ArrayList<Nucleotide>((int)seq.getLength());
    	for(Nucleotide n : seq){
    		list.add(n);
    	}
    	return list;
    }
    @Test
    public void decode(){
        List<Nucleotide> expected = Nucleotides.parse(gappedBasecalls);
        assertEquals(expected, asList(sut));
    }
    
    @Test
    public void getNumberOfGaps(){
        assertEquals(2, sut.getNumberOfGaps());
    }
    
    @Test
    public void getNumberOfGapsUntil(){
        assertEquals("before any gaps", 0, sut.getNumberOfGapsUntil(5));
        assertEquals("on the gap", 1, sut.getNumberOfGapsUntil(8));
        assertEquals("after 1 gap", 1, sut.getNumberOfGapsUntil(9));
        assertEquals("after all gaps", 2, sut.getNumberOfGapsUntil((int)sut.getLength()-1));
    }
    
    @Test
    public void isGap(){
    	Set<Integer> gaps = new HashSet<Integer>(sut.getGapOffsets());
    	
    	for(int i=0; i< sut.getLength(); i++){
    		assertEquals(gaps.contains(Integer.valueOf(i)), sut.isGap(i));
    	}
    }
    
    @Test
    public void getGapOffsets(){
    	assertEquals(Arrays.asList(8, 16), sut.getGapOffsets());
    }
    
    @Test
    public void getLength(){
    	assertEquals(18, sut.getLength());
    }
    
    @Test
    public void getUngappedLength(){
    	assertEquals(16, sut.getUngappedLength());
    }
    
    @Test
    public void getUngappedOffsetFor(){
        assertEquals("before any gaps", 5, sut.getUngappedOffsetFor(5));
        assertEquals("on the gap", 7, sut.getUngappedOffsetFor(8));
        assertEquals("after 1 gap", 8, sut.getUngappedOffsetFor(9));
        assertEquals("after all gaps", sut.getLength()-3, sut.getUngappedOffsetFor((int)sut.getLength()-1));
    }
    
    @Test
    public void getGappedOffsetFor(){
        assertEquals("before any gaps", 5, sut.getGappedOffsetFor(5));
        assertEquals("after 1 gap", 9, sut.getGappedOffsetFor(8));
        assertEquals("after all gaps", 17, sut.getGappedOffsetFor(15));
    }
    
    @Test
    public void toUngappedRange(){
        // private String gappedBasecalls = "ACGTACGT-ACGTACG-T";
        
        assertEquals(Range.of(0, 5), sut.toUngappedRange(Range.of(0,5)));
        assertEquals(Range.of(8,11), sut.toUngappedRange(Range.of(9,12)));
        assertEquals(Range.of(0,15), sut.toUngappedRange(Range.of(0,17)));
        
    }
    
    @Test(expected = NullPointerException.class)
    public void toUngappedRangeNullRangeShouldThrowNPE(){
        sut.toGappedRange(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void toGappedRangeNullRangeShouldThrowNPE(){
        sut.toGappedRange(null);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void toGappedRangeNegativeEndRangeShouldThrowIndexOutOfBounds(){
        sut.toGappedRange(Range.of(-10, -1));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void toUngappedRangeNegativeEndRangeShouldThrowIndexOutOfBounds(){
        sut.toUngappedRange(Range.of(-10, -1));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void toGappedRangeNegativeBeginRangeShouldThrowIndexOutOfBounds(){
        sut.toGappedRange(Range.of(-10, 1));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void toUngappedRangeNegativeBeginRangeShouldThrowIndexOutOfBounds(){
        sut.toUngappedRange(Range.of(-10, 1));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void toGappedRangeEndTooBigShouldThrowIndexOutOfBounds(){
        sut.toGappedRange(Range.of(1, 100));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void toUngappedRangeEndTooBigShouldThrowIndexOutOfBounds(){
        sut.toUngappedRange(Range.of(1, 100));
    }
    
    @Test
    public void toUngappedRangeSafeEndTooBigShouldNotThrowExceptionAndReturnLastOffset(){
        assertEquals(Range.of(1,15), sut.toUngappedRangeSafe(Range.of(1, 100)));
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void toGappedRangeBeginTooBigShouldThrowIndexOutOfBounds(){
        sut.toGappedRange(Range.of(99, 100));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void toUngappedRangeBeginTooBigShouldThrowIndexOutOfBounds(){
        sut.toUngappedRange(Range.of(99, 100));
    }
    
    @Test
    public void toGappedRange(){
        // private String gappedBasecalls = "ACGTACGT-ACGTACG-T";
        
        assertEquals(Range.of(0, 5), sut.toGappedRange(Range.of(0,5)));
        assertEquals(Range.of(9,12), sut.toGappedRange(Range.of(8,11)));
        assertEquals(Range.of(0,17), sut.toGappedRange(Range.of(0,15)));
        
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getGappedOffsetBeyondLengthShouldThrowException(){
    	sut.getGappedOffsetFor(100);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getGappedOffsetEqualToLengthShouldThrowException(){
    	sut.getGappedOffsetFor((int)sut.getUngappedLength());
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getGappedOffsetWithNegativeValueShouldThrowException(){
    	sut.getGappedOffsetFor(-1);
    }
    
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getUngappedOffsetBeyondLengthShouldThrowException(){
    	sut.getUngappedOffsetFor(100);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getUngappedOffsetEqualToLengthShouldThrowException(){
    	sut.getUngappedOffsetFor((int)sut.getLength());
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getUngappedOffsetWithNegativeValueShouldThrowException(){
    	sut.getUngappedOffsetFor(-1);
    }
    
    @Test
    public void get(){
    	List<Nucleotide> list = asList(sut);
    	for(int i=0; i<list.size(); i++){
    		assertEquals(list.get(i), sut.get(i));
    	}
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void getWithNegativeValueShouldThrowException(){
    	sut.get(-1);
    }
    
    @Test
    public void iterator(){
        Iterator<Nucleotide> expected = Nucleotides.parse(gappedBasecalls).iterator();
        Iterator<Nucleotide> actual = sut.iterator();
        assertTrue(actual.hasNext());
        while(actual.hasNext()){
            assertEquals(expected.next(), actual.next());
        }
        assertFalse(expected.hasNext());
    }
    
    @Test
    public void rangedIterator(){
        Iterator<Nucleotide> expected = Nucleotides.parse(gappedBasecalls)
        								.subList(5, 11)
        								.iterator();
        Iterator<Nucleotide> actual = sut.iterator(Range.of(5,10));
        assertTrue(actual.hasNext());
        while(actual.hasNext()){
            assertEquals(expected.next(), actual.next());
        }
        assertFalse(expected.hasNext());
    }
    
    @Test
    public void testToString(){
        assertEquals(gappedBasecalls, sut.toString());
    }
    
    @Test
    public void getNumberOfLeadingGaps(){
    	NucleotideSequence seq = new NucleotideSequenceBuilder("----ACGT").build();
    	assertEquals(4, seq.getGappedOffsetFor(0));
    }
    
    @Test
    public void toBuilder(){
    	assertEquals(sut, sut.toBuilder().build());
    }
    
}
