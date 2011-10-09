package org.jcvi.common.annotation.hmm;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.annotation.hmm.ViterbiPathDecoder;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestViterbiPath extends AbstractTestViterbi{

	private ViterbiPathDecoder viterbi;
	
	@Before
	public void createViterbiObject(){
		viterbi = new ViterbiPathDecoder(HMM);
	}
	@Test
	public void TAGCTGATCGT(){
		NucleotideSequence sequence = NucleotideSequenceFactory.create("TAGCTGATCGT");
		List<Integer> path =viterbi.decodePath(sequence);
		assertEquals(Arrays.asList(0, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 0), path);
	}
	
	@Test
	public void ATCGTA(){
		NucleotideSequence sequence = NucleotideSequenceFactory.create("ATCGTA");
		List<Integer> path =viterbi.decodePath(sequence);
		assertEquals(Arrays.asList(0, 1, 2, 2, 2, 2, 4, 0), path);
	}
	
	@Test
	public void CGATTCGC(){
		NucleotideSequence sequence = NucleotideSequenceFactory.create("CGATTCGC");
		List<Integer> path =viterbi.decodePath(sequence);
		assertEquals(Arrays.asList(0, 1, 3,3,3,3,3,3,4,0), path);
	}
	
	@Test
	public void GCATCGGATC(){
		NucleotideSequence sequence = NucleotideSequenceFactory.create("GCATCGGATC");
		List<Integer> path =viterbi.decodePath(sequence);
		assertEquals(Arrays.asList(0, 1, 3,3,3,3,3,3,3,3,4,0), path);
	}
}
