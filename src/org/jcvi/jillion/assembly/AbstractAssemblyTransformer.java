package org.jcvi.jillion.assembly;

import java.net.URI;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code AbstractAssemblyTransformer} is an abstract class
 * that implements all the methods of {@link AssemblyTransformer}
 * but the methods are all no-ops.
 * 
 * This way you only have to implement the methods you care about.
 * @author dkatzel
 *
 */
public abstract class AbstractAssemblyTransformer implements AssemblyTransformer{

	@Override
	public void referenceOrConsensus(String id,
			NucleotideSequence gappedReference) {
		//no-op
	}

	@Override
	public void endAssembly() {
		//no-op
	}

	@Override
	public void notAligned(String id, NucleotideSequence nucleotideSequence,
			QualitySequence qualitySequence, PositionSequence positions, URI uri) {
		//no-op
	}

	@Override
	public void aligned(String readId, NucleotideSequence nucleotideSequence,
			QualitySequence qualitySequence, PositionSequence positions,
			URI sourceFileUri, String referenceId, long gappedStartOffset,
			Direction direction, NucleotideSequence gappedSequence,
			ReadInfo readInfo) {
		//no-op
	}

	@Override
	public void assemblyCommand(String name, String version, String parameters) {
		//no-op
	}

	@Override
	public void referenceFile(URI uri) {
		//no-op
	}

	@Override
	public void readFile(URI uri) {
		//no-op
	}

}
