package org.jcvi.jillion.assembly.util;

import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;


import org.jcvi.jillion.core.residue.nt.Nucleotide;

public class SliceCollectors {

	public static Collector<SliceElement, ?, Slice> toSlice(){
		return Collector.of(()-> new SliceBuilderCombiner(), 
				SliceBuilderCombiner::add, 
				SliceBuilderCombiner::combine,
				SliceBuilderCombiner::build,
				Characteristics.UNORDERED, Characteristics.CONCURRENT);
	}
	public static Collector<SliceElement, ?, Slice> toSlice(int capacity){
		return Collector.of(()-> new SliceBuilderCombiner(capacity), 
				SliceBuilderCombiner::add, 
				SliceBuilderCombiner::combine,
				SliceBuilderCombiner::build,
				Characteristics.UNORDERED, Characteristics.CONCURRENT);
	}
	public static Collector<SliceElement, ?, Slice> toSlice(Nucleotide consensus){
		Objects.requireNonNull(consensus);
		
		return Collector.of(()-> new SliceBuilderCombiner(consensus), 
				SliceBuilderCombiner::add, 
				SliceBuilderCombiner::combine,
				SliceBuilderCombiner::build,
				Characteristics.UNORDERED, Characteristics.CONCURRENT);
	}
	public static Collector<SliceElement, ?, Slice> toSlice(Nucleotide consensus, int capacity){
		Objects.requireNonNull(consensus);
		
		return Collector.of(()-> new SliceBuilderCombiner(consensus, capacity), 
				SliceBuilderCombiner::add, 
				SliceBuilderCombiner::combine,
				SliceBuilderCombiner::build,
				Characteristics.UNORDERED, Characteristics.CONCURRENT);
	}
	
	private static class SliceBuilderCombiner{
		private SliceBuilder builder;
		
		public SliceBuilderCombiner() {
			builder= new SliceBuilder();
		}
		public SliceBuilderCombiner(int capacity) {
			builder= new SliceBuilder(capacity);
		}
		public SliceBuilderCombiner(Nucleotide consensus) {
			builder= new SliceBuilder(consensus);
		}
		public SliceBuilderCombiner(Nucleotide consensus, int capacity) {
			builder= new SliceBuilder(consensus, capacity);
		}
		
		public synchronized void add(SliceElement e) {
			builder.add(e);
		}
		
		public synchronized SliceBuilderCombiner combine(SliceBuilderCombiner other) {
			builder.addAll(other.builder);
			return this;
		}
		
		public synchronized Slice build() {
			return builder.build();
		}
	}
}
