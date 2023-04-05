package org.jcvi.jillion.assembly;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * Wrapper around multiple {@link AssemblyTransformer}s
 * so that they can all be transformed at the same time by a 
 * {@link AssemblyTransformationService}.
 * 
 * @author dkatzel
 * 
 * @since 6.0
 *
 */
public class MultipleAssemblyTransformer implements AssemblyTransformer{

	private final List<AssemblyTransformer> transformers;
	/**
	 * Create a new {@link AssemblyTransformer} that will delegate all calls to the given
	 * transformers.
	 * @param transformers list of transformers; can not be null or have any null elements.
	 * @throws NullPointerException if any transformer is null.
	 */
	public MultipleAssemblyTransformer(AssemblyTransformer... transformers ) {
		this(List.of(transformers));
	}
	/**
	 * Create a new {@link AssemblyTransformer} that will delegate all calls to the given
	 * transformers.
	 * @param transformers list of transformers; can not be null or have any null elements.
	 * @throws NullPointerException if any transformer is null.
	 */
	public MultipleAssemblyTransformer(Iterable<? extends AssemblyTransformer> transformers) {
		this.transformers = new ArrayList<>();
		for(AssemblyTransformer t: transformers) {
			this.transformers.add(Objects.requireNonNull(t));
		}
	}

	@Override
	public void referenceOrConsensus(String id, NucleotideSequence gappedReference, AssemblyTransformerCallback callback) {
		Iterator<AssemblyTransformer> iter = transformers.iterator();
		AssemblyTransformerCallback wrappedCallback = ()->{
			iter.remove();
		};
		
		while(iter.hasNext()) {
			AssemblyTransformer t = iter.next();
			
			t.referenceOrConsensus(id, gappedReference, wrappedCallback);
		}
		if(transformers.isEmpty()) {
			callback.halt();
		}
		
	}

	@Override
	public void endAssembly() {
		transformers.forEach(AssemblyTransformer::endAssembly);
		
	}

	@Override
	public void notAligned(String id, NucleotideSequence nucleotideSequence, QualitySequence qualitySequence,
			PositionSequence positions, URI uri, Object readObject) {
		transformers.forEach(t-> t.notAligned(id, nucleotideSequence, qualitySequence, positions, uri, readObject));
		
	}

	@Override
	public void aligned(String readId, NucleotideSequence nucleotideSequence, QualitySequence qualitySequence,
			PositionSequence positions, URI sourceFileUri, String referenceId, long gappedStartOffset,
			Direction direction, NucleotideSequence gappedSequence, ReadInfo readInfo, Object readObject) {
		transformers.forEach(t->t.aligned(readId, nucleotideSequence, qualitySequence, positions, sourceFileUri, referenceId, gappedStartOffset, direction, gappedSequence, readInfo, readObject));
		
	}

	@Override
	public void assemblyCommand(String name, String version, String parameters) {
		transformers.forEach(t->t.assemblyCommand(name, version, parameters));
		
	}

	@Override
	public void referenceFile(URI uri) {
		transformers.forEach(t->t.referenceFile(uri));
		
	}

	@Override
	public void readFile(URI uri) {
		transformers.forEach(t-> t.readFile(uri));
		
	}

}
