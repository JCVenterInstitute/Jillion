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
package org.jcvi.jillion.experimental.align;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.Residue;
/**
 * {@code AlnWriter} can write aln encoded
 * alignment files like those produced by Clustal.
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} to be written.
 * @param <S> type of {@link Sequence} to be written.
 */
public interface AlnWriter<R extends Residue, S extends Sequence<R>> extends Closeable {
	/**
	 * Write the given (full length) sequence
	 * in aln format.  The {@link AlnWriter} will
	 * handle breaking the sequence up into groups
	 * and computing conservation etc.
	 * @param id the id of the sequence;
	 * can not be null, must be unique from
	 * all the other sequences being written to the aln.
	 * @param sequence the sequence to write.  If this sequence
	 * is empty, then it is ignored.  Any non-empty sequences
	 * written all must have the same (gapped) length.
	 * @throws IOException if there is a problem writing the sequence.
	 * @throws NullPointerException if either parameter is null
	 * @throws IllegalArgumentException if this method
	 * is called more than once with the same id.
	 */
	void write(String id, S sequence) throws IOException;

	void writeHeader(String header);
	
}
