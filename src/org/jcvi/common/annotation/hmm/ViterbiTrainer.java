package org.jcvi.common.annotation.hmm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;

public class ViterbiTrainer implements HmmTrainer<Nucleotide>{

	private final int numberOfIterations;
	
	
	public ViterbiTrainer(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}


	@Override
	public Hmm<Nucleotide> train(Hmm<Nucleotide> initialModel,
			Collection<? extends Sequence<Nucleotide>> trainingSequences) {
		Hmm<Nucleotide> currentModel = initialModel;
		
		for(int i=0; i<numberOfIterations; i++){
			List<LabeledSequence> labeledSequences = new ArrayList<LabeledSequence>();
			ViterbiPathDecoder pathDecoder = new ViterbiPathDecoder(currentModel);
			for(Sequence<Nucleotide> sequence : trainingSequences){
				List<Integer> path = pathDecoder.decodePath(sequence);
				labeledSequences.add(new LabeledSequence(sequence, path));
			}
			currentModel = updateModel(labeledSequences, currentModel);
		}
		return currentModel;
	}
	
	private Hmm<Nucleotide> updateModel(List<LabeledSequence> labeledSequences,
			Hmm<Nucleotide> previousModel) {
		int numberOfPossibleBases = 4; //acgt only
		int numberOfStates = previousModel.getNumberOfStates();
		int[][] emissionCounts = new int[numberOfStates][numberOfPossibleBases];
		int[][] transitionCounts = new int[numberOfStates][numberOfStates];
		
		for(LabeledSequence labeledSequence : labeledSequences){
			Iterator<Nucleotide> baseIterator =labeledSequence.getSequence().iterator();
			Iterator<Integer> pathIterator =labeledSequence.getPath().iterator();
			//path should always start at initial state
			int previousState =pathIterator.next();
			
			while(baseIterator.hasNext()){
				Nucleotide base = baseIterator.next();
				int currentState = pathIterator.next();				
				transitionCounts[previousState][currentState]++;				
				emissionCounts[currentState][getIndexFor(base)]++;
				previousState = currentState;
			}
			int finalState = pathIterator.next();
			transitionCounts[previousState][finalState]++;
		}
		NucleotideHmmBuilder hmmBuilder = new NucleotideHmmBuilder(numberOfStates);
		for(int i=0; i< numberOfStates; i++){			
			double totalEmissionCountsForState=0;
			for(int j =0; j<emissionCounts[i].length; j++){
				totalEmissionCountsForState += emissionCounts[i][j];
			}
			if(totalEmissionCountsForState ==0){
				//we didn't use this state this time... use previous model?
				HmmState<Nucleotide> previousModelState = previousModel.getState(i);
				hmmBuilder.addProbability(i, Nucleotide.Adenine, previousModelState.getProbabilityOf(Nucleotide.Adenine));
				hmmBuilder.addProbability(i, Nucleotide.Cytosine,previousModelState.getProbabilityOf(Nucleotide.Cytosine));
				hmmBuilder.addProbability(i, Nucleotide.Guanine, previousModelState.getProbabilityOf(Nucleotide.Guanine));
				hmmBuilder.addProbability(i, Nucleotide.Thymine, previousModelState.getProbabilityOf(Nucleotide.Thymine));
		
			}else{
				hmmBuilder.addProbability(i, Nucleotide.Adenine, computeProbability( emissionCounts[i][0], totalEmissionCountsForState));
				hmmBuilder.addProbability(i, Nucleotide.Cytosine,computeProbability(  emissionCounts[i][1], totalEmissionCountsForState));
				hmmBuilder.addProbability(i, Nucleotide.Guanine, computeProbability( emissionCounts[i][2], totalEmissionCountsForState));
				hmmBuilder.addProbability(i, Nucleotide.Thymine, computeProbability( emissionCounts[i][3], totalEmissionCountsForState));
			}
		}
		for(int i=0; i< numberOfStates; i++){
			double totalNumberOfTransitionsForThisState=0;
			for(int j=0; j<numberOfStates; j++){
				totalNumberOfTransitionsForThisState += transitionCounts[i][j];
			}
			for(int j=0; j<numberOfStates; j++){
				if(totalNumberOfTransitionsForThisState ==0){
					//we didn't do any transitions from this state... use previous model
					hmmBuilder.addTransition(i, j, previousModel.getTransitionProbabilityOf(i, j));
				}else{
					hmmBuilder.addTransition(i, j,computeProbability( transitionCounts[i][j], totalNumberOfTransitionsForThisState));
				}
			}
		}
		
		return hmmBuilder.build();
	}
	private double computeProbability(int numerator, double denominator){
		if(denominator ==0){
			return 0;
		}
		return numerator/denominator;
	}
	private int getIndexFor(Nucleotide base){
		switch(base){
		case Adenine : return 0;
		case Cytosine : return 1;
		case Guanine : return 2;
		case Thymine : return 3;
		default : throw new IllegalArgumentException("illegal base "+ base);
		
		}
	}
}
