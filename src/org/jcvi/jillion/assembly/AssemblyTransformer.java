/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly;

import java.net.URI;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.INucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

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
	
	interface AssemblyTransformerCallback{
		void halt();
	}
	/**
	 * The reference or consensus sequence used in the assembly.
	 * @param id the reference or contig consensus id; can not be null.
	 * @param gappedReference the gapped {@link NucleotideSequence}
	 * @param callback the callback object
	 */
	void referenceOrConsensus(String id, INucleotideSequence<?,?> gappedReference, AssemblyTransformerCallback callback);
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
	 * @param readObject the input object of the read.  Usually this shouldn't be used, 
	 * however sometimes if you may need to know additional property information from the original object 
	 * and if you know the type you can downcast this object to get it.
	 */
	void notAligned(String id, NucleotideSequence nucleotideSequence,
			QualitySequence qualitySequence, PositionSequence positions, URI uri, Object readObject);
	/**
	 * This read aligned to a particular reference at the given location.
	 * @param readId the read id.
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
	 * must be mentioned previously by a call to {@link #referenceOrConsensus(String, NucleotideSequence)}.
	 * @param gappedStartOffset the gapped start offset (0-based)
	 * of the read into the gapped reference or consensus sequence.
	 * @param direction the read direction.
	 * @param gappedSequence the gapped sequence of this read
	 * to align to the reference/consensus
	 * @param readInfo the {@link ReadInfo} for this read.
	 * @param readObject the input object of the read.  Usually this shouldn't be used, 
	 * however sometimes if you may need to know additional property information from the original object 
	 * and if you know the type you can downcast this object to get it.
	 * 
	 */
	void aligned(String readId, NucleotideSequence nucleotideSequence,
			QualitySequence qualitySequence, PositionSequence positions,
			URI sourceFileUri, String referenceId, long gappedStartOffset,
			Direction direction,
			NucleotideSequence gappedSequence,
			ReadInfo readInfo, Object readObject);
	/**
	 * The command that was run to generate this assembly.
	 * This method will only be called if the command is known.
	 * 
	 * @param name the name of the assembler used.
	 * 
	 * @param version the version of the assembler used.
	 * @param parameters the parameters used in the assembly invocation.
	 */
	void assemblyCommand(String name, String version, String parameters);
	/**
	 * The {@link URI} of the reference file
	 * used in this assembly.
	 * 
	 * @param uri the path to the reference file.
	 */
	void referenceFile(URI uri);
	/**
	 * The {@link URI} of a read file
	 * used in this assembly.  If more than one
	 * read file was used, then this method
	 * may be called more than once.
	 * 
	 * @param uri the path to the read file.
	 */
	void readFile(URI uri);

	
}
