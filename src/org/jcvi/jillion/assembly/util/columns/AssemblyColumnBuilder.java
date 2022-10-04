package org.jcvi.jillion.assembly.util.columns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import org.jcvi.jillion.core.residue.nt.Nucleotide;


public abstract class AssemblyColumnBuilder<E extends AssemblyColumnElement, R extends AssemblyColumn<E>, T extends AssemblyColumnBuilder<E,R, T>> implements Iterable<E>{

	
	public static <E extends AssemblyColumnElement, R extends AssemblyColumn<E>, T extends AssemblyColumnBuilder<E,R, T>>  AssemblyColumnBuilder<E, R,?> createNewBuilder(){
		return new DefaultAssemblyColumnBuilder<E, R>();
	}
	
	public static <E extends QualifiedAssemblyColumnElement, R extends QualifiedAssemblyColumn<E>>   QualifiedAssemblyColumnBuilder<E,R> createNewQualifiedBuilder(){
		return new QualifiedAssemblyColumnBuilder<E, R>();
	}
	private Nucleotide consensus;
	
	private Queue<E> elements = new ConcurrentLinkedQueue<>();
	
	private AssemblyColumnBuilder() {
	}
	private AssemblyColumnBuilder(Nucleotide consensus) {
		this.consensus = consensus;
	}
	protected abstract T getSelf();
	
	protected abstract R create(Nucleotide consensus, Collection<E> elements);
	
	public T add(E element) {
		elements.add(element);
		return getSelf();
	}
	
	public T addAll(Collection<? extends E> element) {
		elements.addAll(element);
		return getSelf();
	}
	public T addAll(T otherBuilder) {
		for(E e : otherBuilder) {
			elements.add(e);
		}
		return getSelf();
	}
	public Iterator<E> iterator(){
		return elements.iterator();
	}
	public R build() {
		return create(consensus, elements);
	}
	
	public static class QualifiedAssemblyColumnBuilder<Q extends QualifiedAssemblyColumnElement, C extends QualifiedAssemblyColumn<Q>> extends AssemblyColumnBuilder<Q, C, QualifiedAssemblyColumnBuilder<Q, C>>{

		@Override
		protected QualifiedAssemblyColumnBuilder<Q, C> getSelf() {
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected C create(Nucleotide consensus, Collection<Q> elements) {
			return (C) new QualifiedAssemblyColumnImpl<Q>(consensus, elements);
		}
		
	}
	
	private static class DefaultAssemblyColumnBuilder<Q extends AssemblyColumnElement, C extends AssemblyColumn<Q>> extends AssemblyColumnBuilder<Q, C, DefaultAssemblyColumnBuilder<Q, C>>{

		@Override
		protected DefaultAssemblyColumnBuilder<Q, C> getSelf() {
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected C create(Nucleotide consensus, Collection<Q> elements) {
			return (C) new AssemblyColumnImpl<Q>(consensus, elements);
		}
		
	}
	
	private static class QualifiedAssemblyColumnImpl<E extends QualifiedAssemblyColumnElement> extends AssemblyColumnImpl<E> implements QualifiedAssemblyColumn<E> {

		public QualifiedAssemblyColumnImpl(Nucleotide consensus, Collection<E> elements) {
			super(consensus, elements);
		}
		
	}
	private static class AssemblyColumnImpl<E extends AssemblyColumnElement> implements AssemblyColumn<E>{
		private List<E> elements;
		private Nucleotide consensus;

		public AssemblyColumnImpl(Nucleotide consensus, Collection<E> elements) {
			this.elements = new ArrayList<>(elements);
			this.consensus = consensus;
		}
		
		@Override
		public Nucleotide getConsensusCall() {
			return consensus;
		}

		@Override
		public Iterator<E> iterator() {
			return elements.iterator();
		}

		@Override
		public int getCoverageDepth() {
			return elements.size();
		}

		@Override
		public Stream<E> elements() {
			return elements.stream();
		}
		
	}
}
