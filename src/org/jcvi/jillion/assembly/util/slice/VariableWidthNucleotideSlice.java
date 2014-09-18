package org.jcvi.jillion.assembly.util.slice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class VariableWidthNucleotideSlice implements VariableWidthSlice<Nucleotide>{

	
	private final List<VariableWidthSliceElement<Nucleotide>> list = new ArrayList<VariableWidthSliceElement<Nucleotide>>();
	
	private VariableWidthNucleotideSlice(Builder builder){
		
		for(Entry<List<Nucleotide>, LongAdder> entry : builder.countMap.entrySet()){
			list.add(new VariableWidthNucleotideSliceElement(entry.getKey(), entry.getValue().intValue()));
		}
		//sort them
		Collections.sort(list);
		
	}
	
	
	
	@Override
	public int getSliceLength() {
		return list.stream().mapToInt(e-> e.getLength()).max().orElse(0);
	}



	@Override
	public int getCoverageDepth() {
		int coverage=0;
		for(VariableWidthSliceElement<Nucleotide> e : list){
			coverage +=e.getCount();
		}
		return coverage;
	}


	public int getCountFor(List<Nucleotide> sliceElementSeq){
		Objects.requireNonNull(sliceElementSeq);
		Optional<VariableWidthSliceElement<Nucleotide>> ret =list.stream()
															.filter(e -> sliceElementSeq.equals(e.get()))
															.findFirst();
		if(ret.isPresent()){
			return ret.get().getCount();
		}
		return 0;
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
		if (obj instanceof VariableWidthNucleotideSlice) {
			VariableWidthNucleotideSlice other = (VariableWidthNucleotideSlice) obj;
			if (!list.equals(other.list)) {
				return false;
			}
			return true;
		}
		VariableWidthSlice<?> other = (VariableWidthSlice<?>) obj;
		return list.equals(other.elements().collect(Collectors.toList()));
	}

	public static class Builder{
		private final int width;
		private final NucleotideSequence gappedReference;
		
		private final Map<List<Nucleotide>, LongAdder> countMap = new ConcurrentHashMap<>();
		
		public Builder(NucleotideSequence gappedReference) {
			Objects.requireNonNull(gappedReference);
			if(gappedReference.getUngappedLength() <0){
				throw new IllegalArgumentException("ungappedWidth must be >=1");
			}
			
			this.width = (int)gappedReference.getLength();
			this.gappedReference = gappedReference;
		}

		private void assertNoElementsNull(List<Nucleotide> list){
			for(Nucleotide n : list){
				Objects.requireNonNull(n);
			}
		}
		public Builder add(Iterator<Nucleotide> iter){
			int count=0;
			List<Nucleotide> list = new ArrayList<>(width);
			while(iter.hasNext() && count < width){
				list.add(iter.next());
				count++;
			}
			if(count == width){
				return add(list);
			}
			//else skip
			return this;
		}
		public Builder add(Nucleotide...nucleotides){
			return add(Arrays.asList(nucleotides));
		}
		public Builder add(List<Nucleotide> list){
			if(list.size() != width){
				throw new IllegalArgumentException("width does is not length " + width + " : " + list);
			}			
			assertNoElementsNull(list);
			countMap.computeIfAbsent(list, k -> new LongAdder()).increment();
			
			return this;
		}
		
		public VariableWidthNucleotideSlice build(){
			return new VariableWidthNucleotideSlice(this);
		}

		public Builder add(int i, NucleotideSequence seq) {
			List<Nucleotide> list = new ArrayList<>();
			for(Nucleotide n : seq){
				list.add(n);
			}
			countMap.computeIfAbsent(list, k -> new LongAdder()).add(i);
			return this;
		}

		public void skipBases(int frame, Iterator<Nucleotide> iter) {
			int gappedOffset = gappedReference.getGappedOffsetFor(frame -1);
			int numberOfBasesToSkip = width - gappedOffset;
			for(int i=0; iter.hasNext() && i<numberOfBasesToSkip; i++){
				iter.next();
			}
			
		}

		public void skipBases(Iterator<Nucleotide> iter) {
			for(int i=0; iter.hasNext() && i<width; i++){
				iter.next();
			}
			
		}
	}

	@Override
	public String toString() {
		return "VariableWidthNucleotideSlice [list=" + list + "]";
	}

}
