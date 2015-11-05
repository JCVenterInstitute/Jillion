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
package org.jcvi.jillion.assembly.tigr.contig;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code TigrContigVisitor} is a 
 * visitor interface to visit a single
 * contigs inside a TIGR {@literal .contig}
 * encoded file.  The id of the contig
 * has already been given by 
 * {@link TigrContigFileVisitor#visitContig(TigrContigFileVisitor.TigrContigVisitorCallback, String)}.
 * @author dkatzel
 *
 */
public interface TigrContigVisitor {
	/**
	 * Visit the consensus of this contig.
	 * @param consensus the consensus sequence of this contig;
	 * will not be null.
	 */
	void visitConsensus(NucleotideSequence consensus);
	/**
	 * A new read has been detected.
	 * @param readId the read id.
	 * @param gappedStartOffset the start offset of this
	 * read into the gapped consensus in 0-based coordinates.
	 * @param dir the {@link Direction} of this read.
	 * @param validRange the {@link Range} of the "good" portion
	 * of this entire full length, untrimmed read sequence 
	 * that provides coverage to this contig. 
	 * @return a {@link TigrContigReadVisitor} instance
	 * if this read should be visited;
	 * if {@code null} is returned, then
	 * this read will not be visited.
	 */
	TigrContigReadVisitor visitRead(String readId, long gappedStartOffset, Direction dir, Range validRange);
	/**
	 * The parser has stopped 
	 * parsing but has not
	 * actually finished the parsing this contig,
	 * this will happen only if 
	 * a visitor calls {@link org.jcvi.jillion.assembly.tigr.contig.TigrContigFileVisitor.TigrContigVisitorCallback#haltParsing()}.
	 */
	void halted();
	/**
	 * The entire contig has been visited.
	 */
	void visitEnd();
}
