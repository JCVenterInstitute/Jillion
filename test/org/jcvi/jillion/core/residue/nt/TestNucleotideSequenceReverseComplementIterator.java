package org.jcvi.jillion.core.residue.nt;

import org.junit.Test;

import static org.junit.Assert.*;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Iterator;
public class TestNucleotideSequenceReverseComplementIterator {

	@Test
	public void empty() {
		NucleotideSequence seq = NucleotideSequence.of("");
		assertFalse(seq.reverseComplementIterator().hasNext());
	}
	
	@Test
	public void oneBase() {
		NucleotideSequence seq = NucleotideSequence.of("A");
		assertIterationOrder("T", seq.reverseComplementIterator());
	}
	@Test
	public void twoBases() {
		NucleotideSequence seq = NucleotideSequence.of("AA");
		assertIterationOrder("TT", seq.reverseComplementIterator());
	}
	@Test
	public void severalBases() {
		NucleotideSequence seq = NucleotideSequence.of("TCAG");
		assertIterationOrder("CTGA", seq.reverseComplementIterator());
	}
	@Test
	public void hasGaps() {
		NucleotideSequence seq = NucleotideSequence.of("T-CAG");
		assertIterationOrder("CTG-A", seq.reverseComplementIterator());
	}
	@Test
	public void ns() {
		NucleotideSequence seq = NucleotideSequence.of("TNAG");
		assertIterationOrder("CTNA", seq.reverseComplementIterator());
	}
	
	private void assertIterationOrder(String seq, Iterator<Nucleotide> iter) {
		CharacterIterator charIter = new StringCharacterIterator(seq);
		while(iter.hasNext()) {
			
			Nucleotide n = iter.next();
			assertEquals(charIter.current(), n.getCharacter().charValue());
			charIter.next();
			
		}
		assertEquals(CharacterIterator.DONE, charIter.next());
	}
}
