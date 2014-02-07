package org.jcvi.jillion.assembly;

import java.net.URI;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
/**
 * {@code AssemblyTransformer} is an interface
 * to transform one assembly format into another.
 * <p>
 * {@code AssemblyTransformer}
 * is a higher level abstraction of an assembly
 * above a  lower-level Visitor because it hides
 * file format specific information
 * @author dkatzel
 *
 */
public interface AssemblyTransformer {
	/**
	 * The reference or consensus sequence used in the assembly.
	 * @param id the reference or contig consensus id; can not be null.
	 * @param gappedReference the gapped {@link NucleotideSequence}
	 */
	void referenceOrConsensus(String id, NucleotideSequence gappedReference);
	/**
	 * The end of the assembly has been reached.
	 */
	void endAssembly();
	/**
	 * This read did not align to any reference or 
	 * contig consensus.
	 * @param id the read id.
	 * @param nucleotideSequence the {@link NucleotideSequence};
	 * can not be null.
	 * @param qualitySequence the {@link QualitySequence} for this
	 * read; may be {@code null} if not known.
	 * @param positions the {@link PositionSequence} for this
	 * read; may be {@code null} if not known or if there are no
	 * positions (for example if this read is not sanger).
	 * @param uri the {@link URI} for the location of the file
	 * that contains this read; may be {@code null} if not known.
	 */
	void notAligned(String id, NucleotideSequence nucleotideSequence,
			QualitySequence qualitySequence, PositionSequence positions, URI uri);
	/**
	 * This read aligned to a particular reference at the given location.
	 * @param id the read id.
	 * @param nucleotideSequence the {@link NucleotideSequence};
	 * can not be null.
	 * @param qualitySequence the {@link QualitySequence} for this
	 * read; may be {@code null} if not known.
	 * @param positions the {@link PositionSequence} for this
	 * read; may be {@code null} if not known or if there are no
	 * positions (for example if this read is not sanger).
	 * @param sourceFileUri the {@link URI} for the location of the file
	 * that contains this read; may be {@code null} if not known.
	 * @param referenceId the reference id this read aligned to.  This id
	 * must be mentioned previously by a call to {@link #referenceOrConsensus(String, NucleotideSequence).
	 * @param gappedStartOffset the gapped start offset (0-based)
	 * of the read into the gapped reference or consensus sequence.
	 * @param direction the read direction.
	 * @param gappedSequence the gapped sequence of this read
	 * to align to the reference/consensus
	 * @param readInfo the {@link ReadInfo} for this read.
	 */
	void aligned(String readId, NucleotideSequence nucleotideSequence,
			QualitySequence qualitySequence, PositionSequence positions,
			URI sourceFileUri, String referenceId, long gappedStartOffset,
			Direction direction,
			NucleotideSequence gappedSequence,
			ReadInfo readInfo);
	/**
	 * The command that was run to generate this assembly.
	 * This method will only be called if the command is kown.
	 * @param name
	 * @param version
	 * @param parameters
	 */
	void assemblyCommand(String name, String version, String parameters);
	/**
	 * The {@link URI} of the reference file
	 * used in this assembly.
	 * @param uri
	 */
	void referenceFile(URI uri);
	/**
	 * The {@link URI} of a read file
	 * used in this assembly.  If more than one
	 * read file was used, then this method
	 * may be called more than once.
	 * @param uri
	 */
	void readFile(URI uri);

	
}
