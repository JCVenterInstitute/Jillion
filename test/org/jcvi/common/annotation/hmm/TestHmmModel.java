package org.jcvi.common.annotation.hmm;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.annotation.Gene;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestHmmModel {

	@Test
	public void h3(){
		NucleotideSequence sequence = new NucleotideSequenceBuilder("AATCGGGATTAGCTACGTAGCTACTTATCGATCG").build();
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
		NucleotideSequence sequence = new NucleotideSequenceBuilder("AATCATGGGGATTAGCTACGTATAGACTTATCGA").build();
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
		NucleotideSequence sequence = new NucleotideSequenceBuilder("CATGGCGTGCAGCCCATTAGTTACTGTACCTAGT").build();
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
		NucleotideSequence sequence = new NucleotideSequenceBuilder("GCATGGTCTTAGGTGTATCGACAGTGTAGTAATC").build();
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
	
	@Test
	public void h27_oneExon(){
		NucleotideSequence sequence = new NucleotideSequenceBuilder("AATCATGGGGATTAGCTACGTATAGACTTATCGA").build();
		List<Gene> genes = Arrays.asList(
					new Gene("gene 1", Range.buildRange(4,24)));
		int[] expected = new int[]{
				1,1,1,1,2,3,4,5,6,7,5,6,7,5,6,7,5,6,7,5,6,7,8,9,10,1,1,1,1,1,1,1,1,1
		};
		
		int[] actual = HmmModels.H27.labelSequence(sequence, genes);
		
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void h27_oneIntron(){
		NucleotideSequence sequence = new NucleotideSequenceBuilder("CATGGCGTGCAGCCCATTAGTTACTGTACCTAGT").build();
		List<Gene> genes = Arrays.asList(
					new Gene("gene 1", Range.buildRange(1,5), Range.buildRange(20,32)));
		int[] expected = new int[]{
			  //C A T G G C G  T  G  C  A  G  C  C  C  A  T  T  A  G  T T A C T G T A C C T A G  T
			  //N E E E E E I  I  I  I  I  I  I  I  I  I  I  I  I  I  E E E E E E E E E E E E E  N
				1,2,3,4,5,6,18,19,20,20,20,20,20,20,20,20,20,20,21,22,7,5,6,7,5,6,7,5,6,7,8,9,10,1
			};
		
		int[] actual = HmmModels.H27.labelSequence(sequence, genes);
		
		assertArrayEquals(expected, actual);
	}
	@Test
	public void h27_manyIntrons(){
		NucleotideSequence sequence = new NucleotideSequenceBuilder("GCATGGTCTTAGGTGTATCGACAGTGTAGTAATC").build();
		List<Gene> genes = Arrays.asList(
					new Gene("gene 1", 
							Range.buildRange(2,4),
							Range.buildRange(12,13),
							Range.buildRange(24),
							Range.buildRange(29,31)							
					));
		int[] expected = new int[]{
			  //G C A T G G  T  C  T  T  A  G  G T G  T  A  T  C  G  A  C  A  G  T G  T  A  G  T A A  T C
			  //N N E E E I  I  I  I  I  I  I  E E I  I  I  I  I  I  I  I  I  I  E I  I  I  I  E E E  N N
				1,1,2,3,4,23,24,25,25,25,26,27,5,6,18,19,20,20,20,20,20,20,21,22,7,23,24,26,27,8,9,12,1,1,
		};
		
		int[] actual = HmmModels.H27.labelSequence(sequence, genes);
		
		assertArrayEquals(expected, actual);
	}
}
