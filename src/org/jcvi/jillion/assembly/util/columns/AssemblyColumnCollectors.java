package org.jcvi.jillion.assembly.util.columns;

import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import org.jcvi.jillion.assembly.util.columns.AssemblyColumnBuilder.QualifiedAssemblyColumnBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

public class AssemblyColumnCollectors {

	
//	public static <E extends AssemblyColumnElement, C extends AssemblyColumn<E>> Collector<E, ?, C> toAssemblyColumn(){
//		return Collector.of(()-> new AssemblyColumnBuilderCombiner<E, C, ? extends AssemblyColumnBuilder<E,C,>>>(AssemblyColumnBuilder.createNewBuilder()), 
//				AssemblyColumnBuilderCombiner::add, 
//				AssemblyColumnBuilderCombiner::combine,
//				AssemblyColumnBuilderCombiner::build,
//				Characteristics.UNORDERED, Characteristics.CONCURRENT);
//	}
	public static <E extends QualifiedAssemblyColumnElement, C extends QualifiedAssemblyColumn<E>> Collector<E, ?, C> toQualifiedAssemblyColumn(){
		
		return Collector.of(()-> {
			QualifiedAssemblyColumnBuilder<E, C> builder = AssemblyColumnBuilder.createNewQualifiedBuilder();
			
			return new AssemblyColumnBuilderCombiner<>(builder);
		},
				AssemblyColumnBuilderCombiner::add, 
				AssemblyColumnBuilderCombiner::combine,
				AssemblyColumnBuilderCombiner::build,
				Characteristics.UNORDERED, Characteristics.CONCURRENT);
	}
//	
//	private static class DefaultAssemblyColumnCollector<E extends AssemblyColumnElement,
//	B extends AssemblyColumnBuilder<E, C, B>,
//	C extends AssemblyColumn<E>> implements Collector<E, AssemblyColumnBuilder<E,C,B>, C>{
//		
//	}
	
	private static class AssemblyColumnBuilderCombiner<E extends AssemblyColumnElement, C extends AssemblyColumn<E>, X extends AssemblyColumnBuilder<E,C, X>>{
		private X builder;
		
		public AssemblyColumnBuilderCombiner(X builder) {
			this.builder= builder;
		}
		
		
		public void add(E e) {
			builder.add(e);
		}
		
		public AssemblyColumnBuilderCombiner<E,C,X> combine(AssemblyColumnBuilderCombiner<E,C,X> other) {
			builder.addAll(other.builder);
			return this;
		}
		
		public C build() {
			return builder.build();
		}
	}
	
	
}
