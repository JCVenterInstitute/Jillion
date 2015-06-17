package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class AbstractTestSequenceIteratorSubRange {
	String seqString = "A-A-G-TA-C-G-A-C--A-T-A-GA-T-T-AAAAAT--TACGGAG-A-ATAGCTTTGA-GCAAATAACT-TTTATGCAAGCCTTACAACTA-TTGCTTG-AA-G-TGGAG-C-AAG-AGATAAG";
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void iterateRangeTooLongShouldThrowIndexOutOfBoundsException(){
		NucleotideSequence refEncodedSeq = createSequence(seqString);
		
		expectedException.expect(IndexOutOfBoundsException.class);
		 refEncodedSeq.iterator(Range.of(4, 150));
		
		
	}
	
	@Test
	public void iterateRangeWithNegStart(){
		NucleotideSequence refEncodedSeq = createSequence(seqString);
		
		expectedException.expect(IndexOutOfBoundsException.class);
		 refEncodedSeq.iterator(Range.of(-4, 10));
		
		
	}
	
	@Test
	public void iterateRange(){
		NucleotideSequence refEncodedSeq = createSequence(seqString);
		Range range = Range.of(4, 120);
		
		int i=0;
		
		Iterator<Nucleotide> iter = refEncodedSeq.iterator(range);
		
		assertTrue(iter.hasNext());
		while(iter.hasNext()){
			Nucleotide actual = iter.next();
			assertNotNull(""+ i, actual);
			assertEquals(""+ i, refEncodedSeq.get( i+4), actual);
			i++;
		}
		
	}

	protected abstract NucleotideSequence createSequence(String seqString);
}
