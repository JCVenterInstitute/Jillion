package org.jcvi.jillion.assembly.util.slice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

public class SingleNucleotideSlice implements VariableWidthSlice<Nucleotide>{

	private static final int numberOfNucleotideTypes = Nucleotide.values().length;
	
	private final List<VariableWidthSliceElement<Nucleotide>> list = new ArrayList<VariableWidthSliceElement<Nucleotide>>();
	
	private SingleNucleotideSlice(Builder builder){
		for(int i=0; i<numberOfNucleotideTypes; i++){
			int count = builder.counts[i];
			if(count>0){
				list.add(new SingleNucleotideSliceElement(Nucleotide.getByOrdinal(i), count));
			}
		}
	}
	
	
	
	@Override
	public int getCountFor(List<Nucleotide> sliceElementSeq) {
		if(sliceElementSeq.size() !=0){
			return 0;
		}
		for(VariableWidthSliceElement<Nucleotide> e : list){
			if(e.get().equals(sliceElementSeq)){
				return e.getCount();
			}
		}
		return 0;
	}



	@Override
	public int getSliceLength() {
		return 1;
	}



	@Override
	public int getCoverageDepth() {
		int coverage=0;
		for(VariableWidthSliceElement<Nucleotide> e : list){
			coverage +=e.getCount();
		}
		return coverage;
	}



	@Override
	public Stream<VariableWidthSliceElement<Nucleotide>> elements() {
		return list.stream();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + list.hashCode();
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if( !(obj instanceof VariableWidthSlice)){
			return false;
		}
		if (obj instanceof SingleNucleotideSlice) {
			SingleNucleotideSlice other = (SingleNucleotideSlice) obj;
			if (!list.equals(other.list)) {
				return false;
			}
			return true;
		}
		VariableWidthSlice<?> other = (VariableWidthSlice<?>) obj;
		return list.equals(other.elements().collect(Collectors.toList()));
	}

	public static class Builder{
		private int[] counts = new int[numberOfNucleotideTypes];
		
		public Builder add(Nucleotide n){
			counts[n.getOrdinalAsByte()]++;
			return this;
		}
		
		public SingleNucleotideSlice build(){
			return new SingleNucleotideSlice(this);
		}
	}

	@Override
	public String toString() {
		return "SingleNucleotideSlice [list=" + list + "]";
	}

}
