package org.jcvi.common.annotation.hmm;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.annotation.hmm.ViterbiPathDecoder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
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
		NucleotideSequence sequence = new NucleotideSequenceBuilder("TAGCTGATCGT").build();
		List<Integer> path =viterbi.decodePath(sequence);
		assertEquals(Arrays.asList(0, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 0), path);
	}
	
	@Test
	public void ATCGTA(){
		NucleotideSequence sequence = new NucleotideSequenceBuilder("ATCGTA").build();
		List<Integer> path =viterbi.decodePath(sequence);
		assertEquals(Arrays.asList(0, 1, 2, 2, 2, 2, 4, 0), path);
	}
	
	@Test
	public void CGATTCGC(){
		NucleotideSequence sequence = new NucleotideSequenceBuilder("CGATTCGC").build();
		List<Integer> path =viterbi.decodePath(sequence);
		assertEquals(Arrays.asList(0, 1, 3,3,3,3,3,3,4,0), path);
	}
	
	@Test
	public void GCATCGGATC(){
		NucleotideSequence sequence = new NucleotideSequenceBuilder("GCATCGGATC").build();
		List<Integer> path =viterbi.decodePath(sequence);
		assertEquals(Arrays.asList(0, 1, 3,3,3,3,3,3,3,3,4,0), path);
	}
}
