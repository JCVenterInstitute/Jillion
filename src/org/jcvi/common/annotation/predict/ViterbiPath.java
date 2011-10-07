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
		Nucleotide firstBase = sequence.get(0);
		Matrices matrices = new Matrices(hmm, sequenceLength,firstBase);
		
		for(int k=1; k<sequenceLength; k++){			
			Nucleotide base = sequence.get(k);
			for(HmmState<Nucleotide> candidateState : hmm.getEmissionStatesFor(base)){
				int candidateStateIndex = candidateState.getIndex();
				for(HmmState<Nucleotide> nextState: hmm.getTransitionStatesFrom(candidateState)){
					int nextStateIndex = nextState.getIndex();
					double transitionProb = hmm.getTransitionProbabilityOf(candidateStateIndex,nextStateIndex);
					double probabilityOfBasecall = candidateState.getProbabilityOf(base);
					
					double prob = matrices.getProbabilityOfMostProbablePath(candidateStateIndex, k-1)
					                + Math.log10(transitionProb) 
					                + Math.log10(probabilityOfBasecall);
					
					if(prob > matrices.getProbabilityOfMostProbablePath(nextStateIndex,k)){
					    matrices.updateMostProbablePath(k, candidateStateIndex, nextStateIndex, prob);
						
					}
				}
			}
		}
		
		LIFOQueue<Integer> path = new LIFOQueue<Integer>();
		//always end at q0
		path.add(0);
		int currentState = findOptimalPenultimateState(hmm, sequenceLength, numberOfStates, matrices);
		//traceback
		for(int k=sequenceLength-1; k>=0; k--){
			path.add(currentState);
			currentState =matrices.getOptimalPredecessorStateOf(currentState,k);
		}
		path.add(0);
		return asList(path);
	}

    private int findOptimalPenultimateState(Hmm<Nucleotide> hmm,
            int sequenceLength, int numberOfStates, Matrices matrices) {
        int currentState=1;
        for(int i=2; i< numberOfStates; i++){
			double probOfITransitionToFinal = Math.log10(hmm.getTransitionProbabilityOf(i,0));
			double probOfIState = matrices.getProbabilityOfMostProbablePath(i,sequenceLength-1)
			                                + probOfITransitionToFinal;
			
			double probOfCurrentTransitionToFinal = Math.log10(hmm.getTransitionProbabilityOf(currentState,0));
			double probOfCurrentState = 
            			    matrices.getProbabilityOfMostProbablePath(currentState,sequenceLength-1)
            			    +    probOfCurrentTransitionToFinal;
			
			if(probOfIState >  probOfCurrentState){
				currentState=i;
			}
		}
        return currentState;
    }

    private List<Integer> asList(LIFOQueue<Integer> path) {
        //convert from stack to list
		List<Integer> pathList = new ArrayList<Integer>();
		while(!path.isEmpty()){
			pathList.add(path.remove());
		}
        return pathList;
    }

    
	
	private static class Matrices{
	    private final double[][] probabilityOfMostProbPath;
        
        private final Integer[][] stateOfOptimalPredecessor;
        
        public Matrices(Hmm<Nucleotide> hmm, int sequenceLength, Nucleotide firstBase){
            int numberOfStates = hmm.getNumberOfStates();
            probabilityOfMostProbPath = new double[numberOfStates][sequenceLength];            
            stateOfOptimalPredecessor = new Integer[numberOfStates][sequenceLength];
            initialize(numberOfStates, sequenceLength);
            
          //first col must always start at state 0        
            for(int i=1; i<numberOfStates;i++ ){
                HmmState<Nucleotide> state = hmm.getState(i);
                double prob = computeInitialProbability(hmm, firstBase, state);         
                setInitialProbability(i, prob);
            }
        }
        private double computeInitialProbability(Hmm<Nucleotide> hmm,
                Nucleotide base, HmmState<Nucleotide> state) {
            double temp1 = hmm.getTransitionProbabilityOf(0,state.getIndex());
            double temp2 = state.getProbabilityOf(base);
            return Math.log10(temp1) + Math.log10(temp2);
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
        private void setInitialProbability(int i, double prob) {
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
