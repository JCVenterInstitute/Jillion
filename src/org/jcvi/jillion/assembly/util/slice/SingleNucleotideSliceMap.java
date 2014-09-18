package org.jcvi.jillion.assembly.util.slice;

import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class SingleNucleotideSliceMap implements VariableWidthSliceMap<Nucleotide>{

	private final SingleNucleotideSlice[] slices;
	
	private SingleNucleotideSliceMap(Builder builder){
		slices = new SingleNucleotideSlice[builder.builders.length];
		
		for(int i=0; i<slices.length; i++){
			slices[i] = builder.builders[i].build();
		}
	}
	
	@Override
	public VariableWidthSlice<Nucleotide> getSlice(int offset) {
		return slices[offset];
	}

	@Override
	public int getConsensusLength() {
		return slices.length;
	}
	
	
	
	@Override
	public int getNumberOfSlices() {
		//each slice is only 1 so number of slices matches length
		return getConsensusLength();
	}



	public static class Builder{
		private final SingleNucleotideSlice.Builder[] builders;
		
		public Builder(int gappedConsensusLength){
			builders = new SingleNucleotideSlice.Builder[gappedConsensusLength];
			Arrays.parallelSetAll(builders,(i)->new SingleNucleotideSlice.Builder());
		}
		
		public Builder add(int offset, NucleotideSequence seq){
			int currentOffset = offset;
			Iterator<Nucleotide> iter = seq.iterator();
			
			while(iter.hasNext()){
				builders[currentOffset++].add(iter.next());
			}
			
			return this;
		}
		
		public SingleNucleotideSliceMap build(){
			return new SingleNucleotideSliceMap(this);
		}
	}

}
