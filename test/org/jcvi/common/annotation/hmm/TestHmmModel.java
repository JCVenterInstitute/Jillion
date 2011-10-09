package org.jcvi.common.annotation.hmm;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.annotation.Gene;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestHmmModel {

	@Test
	public void h3(){
		NucleotideSequence sequence = NucleotideSequenceFactory
							.create("AATCGGGATTAGCTACGTAGCTACTTATCGATCG");
		List<Gene> genes = Arrays.asList(
								new Gene("gene 1", Range.buildRange(6,12)),
								new Gene("gene 2", Range.buildRange(20,25)));
		int[] expected = new int[]{
				1,1,1,1,1,1,2,2,2,2,2,2,2,1,1,1,1,1,1,1,2,2,2,2,2,2,1,1,1,1,1,1,1,1
		};
		
		int[] actual = HmmModels.H3.labelSequence(sequence, genes);
		
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void h5_oneExon(){
		NucleotideSequence sequence = NucleotideSequenceFactory
			.create("AATCATGGGGATTAGCTACGTATAGACTTATCGA");
		List<Gene> genes = Arrays.asList(
					new Gene("gene 1", Range.buildRange(4,24)));
		int[] expected = new int[]{
				1,1,1,1,2,3,4,2,3,4,2,3,4,2,3,4,2,3,4,2,3,4,2,3,4,1,1,1,1,1,1,1,1,1
			};
		
		int[] actual = HmmModels.H5.labelSequence(sequence, genes);
		
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void h5_oneIntron(){
		NucleotideSequence sequence = NucleotideSequenceFactory
			.create("AATCATGGGGATTAGCTACGTATAGACTTATCGA");
		List<Gene> genes = Arrays.asList(
					new Gene("gene 1", Range.buildRange(1,5), Range.buildRange(20,32)));
		int[] expected = new int[]{
				1,2,3,4,2,3,5,5,5,5,5,5,5,5,5,5,5,5,5,5,4,2,3,4,2,3,4,2,3,4,2,3,4,1
			};
		
		int[] actual = HmmModels.H5.labelSequence(sequence, genes);
		
		assertArrayEquals(expected, actual);
	}
	@Test
	public void h5_manyIntrons(){
		NucleotideSequence sequence = NucleotideSequenceFactory
			.create("GCATGGTCTTAGGTGTATCGACAGTGTAGTAATC");
		List<Gene> genes = Arrays.asList(
					new Gene("gene 1", 
							Range.buildRange(2,4),
							Range.buildRange(12,13),
							Range.buildRange(24),
							Range.buildRange(29,31)							
					));
		int[] expected = new int[]{
				1,1,2,3,4,5,5,5,5,5,5,5,2,3,5,5,5,5,5,5,5,5,5,5,4,5,5,5,5,2,3,4,1,1
			};
		
		int[] actual = HmmModels.H5.labelSequence(sequence, genes);
		
		assertArrayEquals(expected, actual);
	}
	
}
