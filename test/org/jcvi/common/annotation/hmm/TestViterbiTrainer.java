package org.jcvi.common.annotation.hmm;


import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.junit.Test;

public class TestViterbiTrainer extends AbstractTestViterbi{

	private final List<NucleotideSequence> sequences = Arrays.asList(
			NucleotideSequenceFactory.create("TAGCTGATCGT"),
			NucleotideSequenceFactory.create("ATCGTA"),
			NucleotideSequenceFactory.create("CGATTCGC"),
			NucleotideSequenceFactory.create("GCATCGGATC"),
			//added made up sequence will probably throw off model
			NucleotideSequenceFactory.create("ATTCGTACTAGGGA")
			);
	
	@Test
	public void oneIteration(){
		ViterbiTrainer trainer = new ViterbiTrainer(1);
		Hmm<Nucleotide> trainedHmm = trainer.train(HMM, sequences);
		System.out.println(" 1 iteration...");
		System.out.println(trainedHmm);
	}
	
	@Test
	public void twoIterations(){
		ViterbiTrainer trainer = new ViterbiTrainer(2);
		Hmm<Nucleotide> trainedHmm = trainer.train(HMM, sequences);
		System.out.println(" 2 iterations...");
		System.out.println(trainedHmm);
	}
	
	@Test
	public void tenIterations(){
		ViterbiTrainer trainer = new ViterbiTrainer(10);
		Hmm<Nucleotide> trainedHmm = trainer.train(HMM, sequences);
		System.out.println(" 10 iterations...");
		System.out.println(trainedHmm);
	}
}
