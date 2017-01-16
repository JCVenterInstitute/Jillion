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

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.shared.fasta.AbstractFastaRecordBuilder;
/**
 * {@code NucleotideFastaRecordBuilder} is a builder class
 * that makes instances of {@link NucleotideFastaRecord}s.
 * Depending on the different parameters, this builder might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class NucleotideFastaRecordBuilder extends AbstractFastaRecordBuilder<Nucleotide, NucleotideSequence, NucleotideFastaRecord, NucleotideFastaRecordBuilder>{
	/**
	 * Create a new {@link NucleotideFastaRecordBuilder}
	 * instance that has the given id and sequence.  
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the sequence of the fasta record; can not be null.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	public NucleotideFastaRecordBuilder(String id,
			NucleotideSequence sequence) {
		super(id, sequence);
	}
	/**
	 * Convenience constructor that converts a String into
	 * a {@link NucleotideSequence}.  This is the same
	 * as {@link #NucleotideFastaRecordBuilder(String, NucleotideSequence)
	 * new NucleotideFastaRecordBuilder(id, new NucleotideSequenceBuilder(sequence).build())}.
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the nucleotide sequence as a string.  May contain whitespace
	 * which will get removed. can not be null.
	 * @throws IllegalArgumentException if any non-whitespace
     * in character in the sequence can not be converted
     * into a {@link Nucleotide}.
     * @throws NullPointerException if either id or sequence are null.
     * @see NucleotideSequenceBuilder
	 */
	public NucleotideFastaRecordBuilder(String id,
			String sequence) {
		super(id, new NucleotideSequenceBuilder(sequence).build());
	}
	
	@Override
	protected NucleotideFastaRecord createNewInstance(String id,
			NucleotideSequence sequence, String comment) {
		if(comment==null){
			return new UnCommentedNucleotideSequenceFastaRecord(id, sequence);
		}
		return new CommentedNucleotideSequenceFastaRecord(id, sequence,comment);
	}
    @Override
    protected NucleotideFastaRecordBuilder getThis() {
        return this;
    }
    @Override
    protected NucleotideFastaRecordBuilder newBuilder(String id,
            NucleotideSequence seq, String comment) {
        return new NucleotideFastaRecordBuilder(id, seq).comment(comment);
    }


	
}
