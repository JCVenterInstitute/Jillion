package org.jcvi.jillion.assembly;

import java.net.URI;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;

public interface AssemblyTransformer {

	void addReferenceOrConsensus(String id, NucleotideSequence gappedReference);

	void endAssembly();

	void notAligned(String id, NucleotideSequence nucleotideSequence,
			QualitySequence qualitySequence, PositionSequence positions, URI uri);

	void aligned(String id, NucleotideSequence nucleotideSequence,
			QualitySequence qualitySequence, PositionSequence positions,
			URI sourceFileUri, String referenceId, long gappedStartOffset,
			Direction direction,
			ReferenceMappedNucleotideSequence gappedSequence,
			ReadInfo readInfo);

	void assemblyCommand(String name, String version, String parameters);

	void referenceFile(URI uri);

	void readFile(URI uri);

	
}
