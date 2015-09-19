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
package org.jcvi.jillion.fasta.nt;

import java.io.IOException;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.fasta.FastaWriter;
/**
 * {@code NucleotideFastaWriter} is a interface
 * that handles how {@link NucleotideFastaRecord}s
 * are written.
 * @author dkatzel
 *
 */
public interface NucleotideFastaWriter extends FastaWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord>{
	/**
	 * Write the given {@link NucleotideFastaRecord}
	 * (including the optionalComment if there is one).
	 * @param record the {@link NucleotideSequenceFastaRecord}
	 * to write, can not be null.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if record is null.
	 */
	void write(NucleotideFastaRecord record) throws IOException;
	/**
	 * Write the given id and {@link NucleotideSequence}
	 * out as a NucleotideSequenceFastaRecord without a comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link NucleotideSequence} to be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	void write(String id, NucleotideSequence sequence) throws IOException;
	/**
	 * Write the given id and {@link NucleotideSequence}
	 * out as a NucleotideSequenceFastaRecord along with an optional comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link NucleotideSequence} to be written.
	 * @param optionalComment comment to write, if this value is null,
	 * then no comment will be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	void write(String id, NucleotideSequence sequence, String optionalComment) throws IOException;
}
