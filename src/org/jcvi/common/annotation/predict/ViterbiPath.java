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
		
		Matrices matrices = new Matrices(hmm.getNumberOfStates(), sequenceLength);
		
		//first col must always start at state 0
		Nucleotide firstBase = sequence.get(0);
		for(int i=1; i<hmm.getNumberOfStates();i++ ){
			HmmState<Nucleotide> state = hmm.getState(i);
			double prob = computeInitialProbability(hmm, firstBase, state);			
			matrices.setInitialProbability(i, prob);
			
		}
		
		for(int k=1; k<sequenceLength; k++){			
			Nucleotide base = sequence.get(k);
			for(HmmState<Nucleotide> candidateState : hmm.getEmissionStatesFor(base)){
				int candidateStateIndex = candidateState.getIndex();
				for(HmmState<Nucleotide> nextState: hmm.getTransitionStatesFrom(candidateState)){
					int nextStateIndex = nextState.getIndex();
					double transitionProb = hmm.getTransitionProbabilityOf(candidateStateIndex,nextStateIndex);
					double probabilityOfBasecall = candidateState.getProbabilityOf(base);
					double prob = matrices.getProbabilityOfMostProbablePath(candidateStateIndex, k-1) +
									Math.log10(transitionProb) +
									Math.log10(probabilityOfBasecall);
					if(prob > matrices.getProbabilityOfMostProbablePath(nextStateIndex,k)){
					    matrices.updateMostProbablePath(k, candidateStateIndex, nextStateIndex, prob);
						
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
			double probOfI = matrices.getProbabilityOfMostProbablePath(i,sequenceLength-1)+tempI;
			double tempY = Math.log10(hmm.getTransitionProbabilityOf(y,0));
			double probOfY = matrices.getProbabilityOfMostProbablePath(y,sequenceLength-1)+tempY;
			
			if(probOfI >  probOfY){
				y=i;
			}
		}
		//traceback
		for(int k=sequenceLength-1; k>=0; k--){
			path.add(y);
			y =matrices.getOptimalPredecessorStateOf(y,k);
		}
		path.add(0);
		return asList(path);
	}

    private List<Integer> asList(LIFOQueue<Integer> path) {
        //convert from stack to list
		List<Integer> pathList = new ArrayList<Integer>();
		while(!path.isEmpty()){
			pathList.add(path.remove());
		}
        return pathList;
    }

    private double computeInitialProbability(Hmm<Nucleotide> hmm,
            Nucleotide base, HmmState<Nucleotide> state) {
        double temp1 = hmm.getTransitionProbabilityOf(0,state.getIndex());
        double temp2 = state.getProbabilityOf(base);
        return Math.log10(temp1) + Math.log10(temp2);
    }
	
	private static class Matrices{
	    private final double[][] probabilityOfMostProbPath;
        
        private final Integer[][] stateOfOptimalPredecessor;
        
        public Matrices(int numberOfStates, int sequenceLength){
            probabilityOfMostProbPath = new double[numberOfStates][sequenceLength];            
            stateOfOptimalPredecessor = new Integer[numberOfStates][sequenceLength];
            initialize(numberOfStates, sequenceLength);
        }

        /**
         * @param currentState
         * @param k
         */
        public Integer getOptimalPredecessorStateOf(int currentState, int sequenceOffset) {
            return stateOfOptimalPredecessor[currentState][sequenceOffset];            
        }

        /**
         * @param sequenceOffset
         * @param candidateStateIndex
         * @param nextStateIndex
         * @param prob
         */
        public void updateMostProbablePath(int sequenceOffset, int candidateStateIndex,
                int nextStateIndex, double prob) {
            probabilityOfMostProbPath[nextStateIndex][sequenceOffset] = prob;
            stateOfOptimalPredecessor[nextStateIndex][sequenceOffset] = candidateStateIndex;            
        }

        /**
         * @param i
         * @param j
         * @param prob
         */
        public void setInitialProbability(int i, double prob) {
            probabilityOfMostProbPath[i][0] = prob;
            if(probabilityOfMostProbPath[i][0] >Double.NEGATIVE_INFINITY){
                stateOfOptimalPredecessor[i][0]=0;
            }
            
        }

        public double getProbabilityOfMostProbablePath(int stateIndex, int sequenceOffset){
            return probabilityOfMostProbPath[stateIndex][sequenceOffset];
        }
        private void initialize(int numberOfStates, int sequenceLength) {
            for(int k=0; k<sequenceLength; k++ ){
                for(int i=0; i<numberOfStates; i++){
                    probabilityOfMostProbPath[i][k] = Double.NEGATIVE_INFINITY;
                    stateOfOptimalPredecessor[i][k]=null;
                }
            }
        }
	}

}
