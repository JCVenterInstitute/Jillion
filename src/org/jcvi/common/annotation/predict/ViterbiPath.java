package org.jcvi.common.annotation.predict;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.util.LIFOQueue;

public class ViterbiPath implements PathDecoder<Nucleotide>{

	@Override
	public List<Integer> decodePath(Hmm<Nucleotide> hmm, Sequence<Nucleotide> sequence) {
		int sequenceLength = (int)sequence.getLength();
		int numberOfStates = hmm.getNumberOfStates();
		double[][] probabilityOfMostProbPath = new double[hmm.getNumberOfStates()][sequenceLength];
		
		Integer[][] stateOfOptimalPredecessor = new Integer[numberOfStates][sequenceLength];
		//intialize borders of matrices
		for(int k=0; k<sequenceLength; k++ ){
			for(int i=0; i<hmm.getNumberOfStates(); i++){
				probabilityOfMostProbPath[i][k] = Double.NEGATIVE_INFINITY;
				stateOfOptimalPredecessor[i][k]=null;
			}
		}
		//first col must always start at state 0
		Nucleotide firstBase = sequence.get(0);
		for(int i=1; i<hmm.getNumberOfStates();i++ ){
			HmmState<Nucleotide> state = hmm.getState(i);
			//remember log(0) = neg infinity
			double temp1 = hmm.getTransitionProbabilityOf(0,i);
			double temp2 = state.getProbabilityOf(firstBase);
			probabilityOfMostProbPath[i][0] = Math.log10(temp1) +
											Math.log10(temp2);
			if(probabilityOfMostProbPath[i][0] >Double.NEGATIVE_INFINITY){
				stateOfOptimalPredecessor[i][0]=0;
			}
		}
		
		for(int k=1; k<sequenceLength; k++){			
			Nucleotide base = sequence.get(k);
			for(HmmState<Nucleotide> candidateState : hmm.getEmissionStatesFor(base)){
				int candidateStateIndex = candidateState.getIndex();
				for(HmmState<Nucleotide> nextState: hmm.getTransitionStatesFrom(candidateState)){
					int nextStateIndex = nextState.getIndex();
					double temp1 = hmm.getTransitionProbabilityOf(candidateStateIndex,nextStateIndex);
					double temp2 = candidateState.getProbabilityOf(base);
					double prob = probabilityOfMostProbPath[candidateStateIndex][k-1] +
									Math.log10(temp1) +
									Math.log10(temp2);
					if(prob > probabilityOfMostProbPath[nextStateIndex][k]){
						probabilityOfMostProbPath[nextStateIndex][k] = prob;
						stateOfOptimalPredecessor[nextStateIndex][k] = candidateStateIndex;
					}
				
				}
			}
		}
		//find optimal end state
		int y=1;
		LIFOQueue<Integer> path = new LIFOQueue<Integer>();
		//always end at q0
		path.add(0);
		//find optimal penultimate state
		for(int i=2; i< numberOfStates; i++){
			double tempI = Math.log10(hmm.getTransitionProbabilityOf(i,0));
			double probOfI = probabilityOfMostProbPath[i][sequenceLength-1]+tempI;
			double tempY = Math.log10(hmm.getTransitionProbabilityOf(y,0));
			double probOfY = probabilityOfMostProbPath[y][sequenceLength-1]+tempY;
			
			if(probOfI >  probOfY){
				y=i;
			}
		}
		//traceback
		for(int k=sequenceLength-1; k>=0; k--){
			path.add(y);
			Integer temp = stateOfOptimalPredecessor[y][k];
			y= temp;
		}
		path.add(0);
		//convert from stack to list
		List<Integer> pathList = new ArrayList<Integer>();
		while(!path.isEmpty()){
			pathList.add(path.remove());
		}
		return pathList;
	}

}
