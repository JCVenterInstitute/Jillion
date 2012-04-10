package org.jcvi.common.annotation.hmm;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.util.LIFOQueue;
/**
 * {@code ViterbiPathDecoder} uses the Viterbi algorithm
 * to find the most probable path through an {@link Hmm}
 * for the given sequence.
 * @author dkatzel
 */
public class ViterbiPathDecoder implements HmmPathDecoder<Nucleotide>{
    
	private final Hmm<Nucleotide> hmm;
	
	public ViterbiPathDecoder(Hmm<Nucleotide> hmm){
		this.hmm = hmm;
	}
	
	@Override
	public List<Integer> decodePath(Sequence<Nucleotide> sequence) {
		return new Matrices(sequence)
					.computePathViaTraceback();
	}
	/**
	 * Private inner class that keeps track of our 
	 * probability matrix and optimal predecessor matrix.
	 * @author dkatzel
	 *
	 */
	private class Matrices{
		/**
		 * matrix of path probabilities of the most
		 * probable path from the starting state 0
		 * until the current state i which will
		 * emit our current basecall k.
		 */
	    private final double[][] probabilityOfMostProbPath;
        /**
         * Keeps track of our optimal predecessor state
         * which will be used for the traceback
         * at the end of this algorithm.
         */
        private final Integer[][] stateOfOptimalPredecessor;
        /**
         * our sequence to traverse.
         */
        private final Sequence<Nucleotide> sequence;
        
        public Matrices(Sequence<Nucleotide> sequence){
        	this.sequence =sequence;
            int numberOfStates = hmm.getNumberOfStates();
            int sequenceLength = (int) sequence.getLength();
            probabilityOfMostProbPath = new double[numberOfStates][sequenceLength];            
            stateOfOptimalPredecessor = new Integer[numberOfStates][sequenceLength];
            computeMatrixValues(sequence, sequenceLength);
        }
		private void computeMatrixValues(Sequence<Nucleotide> sequence, int sequenceLength) {
			initialize(sequenceLength);
            computeInitialStateProbabilities(sequence.get(0));            
            populate(sequence, hmm);
		}
		 /**
		  * Initialize probability matrix values probability of 0 
		  * (negative infinity in log space) and optimal predecessor
		  * matrix values to null.
		  * @param sequenceLength number of bases in the sequence to traverse
		  * used to determine sizes of matrices.
		  */
		 private void initialize( int sequenceLength) {
            for(int k=0; k<sequenceLength; k++ ){
                for(int i=0; i<hmm.getNumberOfStates(); i++){
                    probabilityOfMostProbPath[i][k] = Double.NEGATIVE_INFINITY;
                    stateOfOptimalPredecessor[i][k]=null;
                }
            }
        }
		/**
		 * Compute the probability values for the first
		 * base in all non-q0 states.
		 * @param sequence
		 */
		private void computeInitialStateProbabilities(
				Nucleotide firstBase) {
          //first col must always start at state 0        
            for(int i=1; i<hmm.getNumberOfStates();i++ ){
                HmmState<Nucleotide> state = hmm.getState(i);
                double prob = computeInitialProbability(hmm, firstBase, state);         
                setInitialProbability(i, prob);
            }
		}
		/**
		 * Computes probability of traversing this HMM
		 * from initial state q0 into the given state for the 
		 * first base in the sequence.
		 * This logic is handled separately because we don't
		 * have to worry about incorporating the probabilities
		 * of previous states since this is the first base.
		 * @return the probability in log space.
		 */
        private double computeInitialProbability(Hmm<Nucleotide> hmm,
                Nucleotide firstBase, HmmState<Nucleotide> state) {
            double temp1 = hmm.getTransitionProbabilityOf(0,state.getIndex());
            double temp2 = state.getProbabilityOfEmitting(firstBase);
            return Math.log10(temp1) + Math.log10(temp2);
        }
        
        /**
         *Set the probability of transitioning from state 0
         *to the given stateIndex.  If this is the most
         *probable transition, update the optimal predecessor
         *matrix.
         */
        private void setInitialProbability(int stateIndex, double probability) {
            probabilityOfMostProbPath[stateIndex][0] = probability;
            if(probabilityOfMostProbPath[stateIndex][0] >Double.NEGATIVE_INFINITY){
                stateOfOptimalPredecessor[stateIndex][0]=0;
            }
        }
        private void updateMostProbablePath(int sequenceOffset, int candidateStateIndex,
                int nextStateIndex, double prob) {
            probabilityOfMostProbPath[nextStateIndex][sequenceOffset] = prob;
            stateOfOptimalPredecessor[nextStateIndex][sequenceOffset] = candidateStateIndex;            
        }
        private double getProbabilityOfMostProbablePath(int stateIndex, int sequenceOffset){
            return probabilityOfMostProbPath[stateIndex][sequenceOffset];
        }
        /**
         * Get the probability of transitioning from
         * the to the next state by computing the probability of 
         * most probable path so far from the first
         * basecall starting at the initial state
         * up to the previous basecall getting to our current state
         * multiplied by the probability of transitioning from the current state
         * to the next state and the probability of the next state emitting
         * the current basecall.
         * @return the probability in log space.
         */
        private double getProbabilityOfNextState(int candidateStateIndex, int sequenceOffset, double transitionProb, double probabilityOfBasecall){
        	return getProbabilityOfMostProbablePath(candidateStateIndex, sequenceOffset-1)
            + Math.log10(transitionProb) 
            + Math.log10(probabilityOfBasecall);
        }
        
       
        /**
         * Populate the matrices by computing the probabilities of all 
         * possible transitions and emissions
         * for all basecalls in the given sequence.
         */
        private void populate(Sequence<Nucleotide> sequence, Hmm<Nucleotide> hmm){
        	int sequenceLength = (int) sequence.getLength();
        	for(int k=1; k<sequenceLength; k++){			
    			Nucleotide base = sequence.get(k);
    			//consider all states that can emit this basecall
    			for(HmmState<Nucleotide> candidateState : hmm.getEmissionStatesFor(base)){
    				int candidateStateIndex = candidateState.getIndex();
    				//consider all states that can be reached from this candidate state.
    				for(HmmState<Nucleotide> nextState: hmm.getTransitionStatesFrom(candidateState)){
    					int nextStateIndex = nextState.getIndex();
    					double transitionProb = hmm.getTransitionProbabilityOf(candidateStateIndex,nextStateIndex);
    					double probabilityOfBasecall = candidateState.getProbabilityOfEmitting(base);
    					
    					double prob = getProbabilityOfNextState(candidateStateIndex, k, transitionProb, probabilityOfBasecall);
    					//if prob is the max probability we've
    					//seen so far
    					//update most probable path
    					if(prob > getProbabilityOfMostProbablePath(nextStateIndex,k)){
    					    updateMostProbablePath(k, candidateStateIndex, nextStateIndex, prob);
    						
    					}
    				}
    			}
    		}
        }
        
        public List<Integer> computePathViaTraceback() {
        	int sequenceLength = (int)sequence.getLength();
    		LIFOQueue<Integer> path = new LIFOQueue<Integer>();
    		//always end at q0
    		path.add(0);
    		int currentState = findOptimalPenultimateState(hmm, sequenceLength);
    		//traceback
    		for(int k=sequenceLength-1; k>=0; k--){
    			path.add(currentState);
    			currentState =getOptimalPredecessorStateOf(currentState,k);
    		}
    		path.add(0);
    		return asList(path);
    	}
        private Integer getOptimalPredecessorStateOf(int currentState, int sequenceOffset) {
            return stateOfOptimalPredecessor[currentState][sequenceOffset];            
        }
        
        private int findOptimalPenultimateState(Hmm<Nucleotide> hmm,
                int sequenceLength) {
        	int numberOfStates = hmm.getNumberOfStates();
            int currentState=1;
            for(int i=2; i< numberOfStates; i++){
    			double probOfITransitionToFinal = Math.log10(hmm.getTransitionProbabilityOf(i,0));
    			double probOfIState = getProbabilityOfMostProbablePath(i,sequenceLength-1)
    			                                + probOfITransitionToFinal;
    			
    			double probOfCurrentTransitionToFinal = Math.log10(hmm.getTransitionProbabilityOf(currentState,0));
    			double probOfCurrentState = 
                			    getProbabilityOfMostProbablePath(currentState,sequenceLength-1)
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
	}

}
