package org.jcvi.common.annotation.hmm;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.util.Builder;
import org.jcvi.common.core.util.JoinedStringBuilder;

public class NucleotideHmmBuilder implements Builder<Hmm<Nucleotide>>{

	private final List<NucleotideHmmStateBuilder> stateBuilders;
	private final Map<Integer, Set<Transition>> transitions;
	
	public NucleotideHmmBuilder(int numberOfStates){
		stateBuilders = new ArrayList<NucleotideHmmBuilder.NucleotideHmmStateBuilder>(numberOfStates);
		for(int i=0; i< numberOfStates; i++){
			stateBuilders.add(new NucleotideHmmStateBuilder(i));
		}
		transitions = new TreeMap<Integer, Set<Transition>>();
		
	}
	public NucleotideHmmBuilder addProbability(int stateIndex, Nucleotide base, double probability){
		stateBuilders.get(stateIndex).addProbability(base, probability);
		return this;
	}
	
	public NucleotideHmmBuilder addTransition(Integer from, Integer to, double probability){
		if(!transitions.containsKey(from)){
			transitions.put(from, new HashSet<Transition>());
		}
		transitions.get(from).add(new Transition(from,to,probability));
		return this;
	}
	@Override
	public Hmm<Nucleotide> build() {
		Map<HmmState<Nucleotide>, Set<HmmState<Nucleotide>>> transitionMap = new TreeMap<HmmState<Nucleotide>, Set<HmmState<Nucleotide>>>();
		List<HmmState<Nucleotide>> states = new ArrayList<HmmState<Nucleotide>>(stateBuilders.size());
		
		Map<Integer, Map<Integer,Double>> transitionProbabilities = new TreeMap<Integer, Map<Integer,Double>>();
		
		for(int i=0; i< stateBuilders.size(); i++){
			HmmState<Nucleotide> state = stateBuilders.get(i).build();
			states.add(state);
		}
		
		for(Entry<Integer,Set<Transition>> entry : transitions.entrySet()){
			Integer fromIndex = entry.getKey();
			HmmState<Nucleotide> from = states.get(fromIndex.intValue());
			Set<HmmState<Nucleotide>> set = new TreeSet<HmmState<Nucleotide>>();
			Map<Integer,Double> transitionProbs = new TreeMap<Integer, Double>();
			transitionProbabilities.put(fromIndex, transitionProbs);
			double totalTransitionProb =0D;
			for(Transition transitionProbability : entry.getValue()){
				int toIndex = transitionProbability.getToIndex();
				double prob = transitionProbability.getProbability();
				totalTransitionProb +=prob;
				
				HmmState<Nucleotide> to = states.get(toIndex);
				set.add(to);
				
				transitionProbs.put(toIndex, prob);
			}
			if(totalTransitionProb != 1D){
				throw new IllegalStateException(
						String.format("transition probabilities for state %d must equal 100%% : %.2f%%", fromIndex, totalTransitionProb));
			}
			transitionMap.put(from, set);
		}
		
		return new NucleotideHmm(states, transitionMap,transitionProbabilities);
	}

	
	private static final class NucleotideHmmState implements HmmState<Nucleotide>{
		private final Map<Nucleotide, Double> probabilities;
		private final int index;
		
		public NucleotideHmmState(int index,Map<Nucleotide, Double> probabilities) {
			this.probabilities = probabilities;
			this.index = index;
		}
		@Override
		public int getIndex() {
			return index;
		}


		@Override
		public double getProbabilityOf(Nucleotide base) {
			if(base ==null){
				throw new NullPointerException("base can not be null");
			}
			if(probabilities.containsKey(base)){
				return probabilities.get(base);
			}
			//anything not in the map must have probability of 0
			return 0;
		}

		@Override
		public Set<Entry<Nucleotide, Double>> getEntrySet() {
			return probabilities.entrySet();
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
			result = prime * result
					+ ((probabilities == null) ? 0 : probabilities.hashCode());
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof NucleotideHmmState)) {
				return false;
			}
			NucleotideHmmState other = (NucleotideHmmState) obj;
			if (index != other.index) {
				return false;
			}
			if (probabilities == null) {
				if (other.probabilities != null) {
					return false;
				}
			} else if (!probabilities.equals(other.probabilities)) {
				return false;
			}
			return true;
		}
		@Override
		public int compareTo(HmmState<Nucleotide> o) {
			return Integer.valueOf(index).compareTo(o.getIndex());
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "[state index=" + index + ", "
					+ probabilities + "]";
		}
		
	}
	
	private static class NucleotideHmmStateBuilder implements Builder<NucleotideHmmState>{
		private final Map<Nucleotide, Double> probabilities;
		private final int index;
		public NucleotideHmmStateBuilder(int index){
			probabilities = new EnumMap<Nucleotide, Double>(Nucleotide.class);
			this.index = index;
		}
		NucleotideHmmStateBuilder addProbability(Nucleotide base, double prob){
			if(base ==null){
				throw new NullPointerException("base can not be null");
			}
			if(prob <0 || prob >1){
				throw new IllegalArgumentException("probability out of bounds : "+ prob);
			}
			probabilities.put(base, prob);
			return this;
		}
		
		@Override
		public NucleotideHmmState build() {
			//verify all probabilities add up to 100%
			double total=0D;
			for(Double prob : probabilities.values()){
				total+=prob;
			}
			//initial/final state doesn't need to have probabilities
			if(index !=0 && total !=1D){
				throw new IllegalStateException(
						String.format("basecall probabilities for state %d must total 100%% : %.2f%%",index, total));
			}
			return new NucleotideHmmState(index,probabilities);
		}
		
	}
	
	private static final class NucleotideHmm implements Hmm<Nucleotide>{

		private final List<HmmState<Nucleotide>> states;
		private final Map<Nucleotide, Set<HmmState<Nucleotide>>> emissionMatrix;
		private final Map<HmmState<Nucleotide>, Set<HmmState<Nucleotide>>> transitionState;
		private final Map<Integer, Map<Integer,Double>> transitionProbabilities;
		
		public NucleotideHmm(List<HmmState<Nucleotide>> states, 
				Map<HmmState<Nucleotide>, Set<HmmState<Nucleotide>>> transitionState,
				Map<Integer, Map<Integer,Double>> transitionProbabilities) {
			this.states = states;
			this.emissionMatrix = new EnumMap<Nucleotide, Set<HmmState<Nucleotide>>>(Nucleotide.class);
			this.transitionProbabilities = transitionProbabilities;
			this.transitionState = transitionState;
			
			for(HmmState<Nucleotide> state : states){
				for(Entry<Nucleotide, Double> entry : state.getEntrySet()){
					Nucleotide base = entry.getKey();
					double prob = entry.getValue();
					//only include probable states
					if(prob > 0){
						if(!emissionMatrix.containsKey(base)){
							emissionMatrix.put(base, new TreeSet<HmmState<Nucleotide>>());
						}
						emissionMatrix.get(base).add(state);
					}
				}
			}
		}
		
		
		@Override
		public int getNumberOfStates() {
			return states.size();
		}

		@Override
		public HmmState<Nucleotide> getState(int i) {
			return states.get(i);
		}

		@Override
		public Set<HmmState<Nucleotide>> getTransitionStatesFrom(
				HmmState<Nucleotide> state) {
			if(state ==null){
				throw new NullPointerException("state can not be null");
			}
			return transitionState.get(state);
		}

		@Override
		public Set<HmmState<Nucleotide>> getEmissionStatesFor(Nucleotide base) {
			if(base ==null){
				throw new NullPointerException("base can not be null");
			}
			return emissionMatrix.get(base);
		}


		@Override
		public double getTransitionProbabilityOf(int from, int to) {
			Integer fromIndex = Integer.valueOf(from);
			if(!transitionProbabilities.containsKey(fromIndex)){
				return 0;
			}
			Map<Integer,Double> probabilies =transitionProbabilities.get(fromIndex);
			Integer toIndex = Integer.valueOf(to);
			if(!probabilies.containsKey(toIndex)){
				return 0;
			}
			return probabilies.get(toIndex);
		}


		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			
			return "NucleotideHmm [states=" + new JoinedStringBuilder(states).glue("\n").build()
			+ "\nemissionMatrix="
					+ new JoinedStringBuilder(emissionMatrix.entrySet()).glue("\n").build()
					+ "\ntransitionState=" + new JoinedStringBuilder(transitionState.entrySet()).glue("\n").build()
					+ "\ntransitionProbabilities=" + new JoinedStringBuilder(transitionProbabilities.entrySet()).glue("\n").build()
					+ "]";
		}
		
		
		
	}
	
	private static final class Transition {

		private final int fromIndex, toIndex;
		private final double probability;
		public Transition(int fromIndex, int toIndex, double probability) {
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.probability = probability;
		}
		/**
		 * @return the fromIndex
		 */
		public int getFromIndex() {
			return fromIndex;
		}
		/**
		 * @return the toIndex
		 */
		public int getToIndex() {
			return toIndex;
		}
		/**
		 * @return the probability
		 */
		public double getProbability() {
			return probability;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + fromIndex;
			long temp;
			temp = Double.doubleToLongBits(probability);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + toIndex;
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Transition)) {
				return false;
			}
			Transition other = (Transition) obj;
			if (fromIndex != other.fromIndex) {
				return false;
			}
			if (Double.doubleToLongBits(probability) != Double
					.doubleToLongBits(other.probability)) {
				return false;
			}
			if (toIndex != other.toIndex) {
				return false;
			}
			return true;
		}
		
		@Override
		public String toString(){
			return String.format("transition %d -> %d (%.2f%%)", 
						fromIndex, toIndex, probability);
		}
		
	}
}
