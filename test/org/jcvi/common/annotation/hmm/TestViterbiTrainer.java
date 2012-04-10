package org.jcvi.common.annotation.hmm;


import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestViterbiTrainer {

	private final List<NucleotideSequence> sequences = Arrays.asList(
			new NucleotideSequenceBuilder("TAGCTGATCGT").build(),
			new NucleotideSequenceBuilder("ATCGTA").build(),
			new NucleotideSequenceBuilder("CGATTCGC").build(),
			new NucleotideSequenceBuilder("GCATCGGATC").build(),
			//added made up sequence will probably throw off model
			new NucleotideSequenceBuilder("ATTCGTACTAGGGA").build()
			);
	
	@Test
	public void oneIteration(){
		ViterbiTrainer trainer = new ViterbiTrainer(1);
		trainer.train(HMM, sequences);
	}
	
	@Test
	public void twoIterations(){
		ViterbiTrainer trainer = new ViterbiTrainer(2);
		trainer.train(HMM, sequences);
	}
	
	@Test
	public void tenIterations(){
		ViterbiTrainer trainer = new ViterbiTrainer(10);
		trainer.train(HMM, sequences);
	}
	
protected static Hmm<Nucleotide> HMM;
	private static double[] getRandomProbabilities(Random random, int n){
		int available = 100;
		double[] probabilities = new double[n];
		for(int i=0; i<n-1 && available>0; i++){
			int value = random.nextInt(available);
			double prob = value/100D;
			probabilities[i]=prob;
			available-=value;
		}
		probabilities[n-1] = available/100D;
		return probabilities;
	}
	
	private static NucleotideHmmBuilder setRandomBasecallProbs(
			NucleotideHmmBuilder builder, int state, Random r){
		double[] probs = getRandomProbabilities(r,4);
		return builder.addProbability(state, Nucleotide.Adenine, probs[0])
		.addProbability(state, Nucleotide.Thymine, probs[1])
		.addProbability(state, Nucleotide.Cytosine, probs[2])
		.addProbability(state, Nucleotide.Guanine, probs[3]);		
	}
	
	private static NucleotideHmmBuilder setRandomTransistionsProbs(
			NucleotideHmmBuilder builder, int state, Random r){
		//ugly but easy to follow without too much method clutter
		switch(state){
		case 1: {
			double[] probs = getRandomProbabilities(r,2);
			builder.addTransition(1, 2, probs[0])
				.addTransition(1, 3, probs[1]);
			}
			break;
		case 2: {
			double[] probs = getRandomProbabilities(r,2);
			builder.addTransition(2, 2, probs[0])
				.addTransition(2, 4, probs[1]);
			}
			break;
		case 3: {
			double[] probs = getRandomProbabilities(r,2);
			builder.addTransition(3, 3, probs[0])
				.addTransition(3, 4, probs[1]);
			}
			break;
		default: throw new IllegalStateException("can not make transitions for state " + state);		
		}
		return builder;
	}
	@BeforeClass
	public static void buildHmm(){
		Random random = new Random(123456);
		random.nextInt(100);
		NucleotideHmmBuilder builder = new NucleotideHmmBuilder(5);
		setRandomBasecallProbs(builder,1,random);
		setRandomBasecallProbs(builder,2,random);
		setRandomBasecallProbs(builder,3,random);
		setRandomBasecallProbs(builder,4,random);
		
		builder.addTransition(0, 1, 1D);			
		builder.addTransition(4, 0, 1D);
				
		setRandomTransistionsProbs(builder, 1, random);
		setRandomTransistionsProbs(builder, 2, random);
		setRandomTransistionsProbs(builder, 3, random);
		
		HMM = builder.build();		
	}
	
}
