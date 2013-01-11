package org.jcvi.common.annotation.hmm;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.junit.BeforeClass;

public abstract class AbstractTestViterbi {

	protected static Hmm<Nucleotide> HMM;
	
	@BeforeClass
	public static void buildHmm(){
		NucleotideHmmBuilder builder = new NucleotideHmmBuilder(5);
		builder.addProbability(1, Nucleotide.Adenine, 0.1D)
				.addProbability(1, Nucleotide.Thymine, 0.3D)
				.addProbability(1, Nucleotide.Cytosine, 0.4D)
				.addProbability(1, Nucleotide.Guanine, 0.2D)
				
				.addProbability(2, Nucleotide.Adenine, 0.35D)
				.addProbability(2, Nucleotide.Thymine, 0.25D)
				.addProbability(2, Nucleotide.Cytosine, 0.15D)
				.addProbability(2, Nucleotide.Guanine, 0.25D)
				
				.addProbability(3, Nucleotide.Adenine, 0.11D)
				.addProbability(3, Nucleotide.Thymine, 0.17D)
				.addProbability(3, Nucleotide.Cytosine, 0.43D)
				.addProbability(3, Nucleotide.Guanine, 0.29D)
				
				.addProbability(4, Nucleotide.Adenine, 0.27D)
				.addProbability(4, Nucleotide.Thymine, 0.14D)
				.addProbability(4, Nucleotide.Cytosine, 0.22D)
				.addProbability(4, Nucleotide.Guanine, 0.37D)
				
				.addTransition(0, 1, 1D)
				.addTransition(1, 2, 0.5D)
				.addTransition(1, 3, 0.5D)
				.addTransition(2, 2, 0.65D)
				.addTransition(2, 4, 0.35D)
				.addTransition(3, 3, 0.8D)
				.addTransition(3, 4, 0.2D)
				.addTransition(4, 0, 1D);
		
		HMM = builder.build();		
	}
}
