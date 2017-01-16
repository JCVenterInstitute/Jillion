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
package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.internal.fasta.aa.CommentedProteinFastaRecord;
import org.jcvi.jillion.internal.fasta.aa.UnCommentedProteinFastaRecord;
import org.jcvi.jillion.shared.fasta.AbstractFastaRecordBuilder;
/**
 * {@code ProteinFastaRecordBuilder} is a Builder class
 * that makes instances of {@link ProteinFastaRecord}s.
 * Depending on the different parameters, this builder might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class ProteinFastaRecordBuilder extends AbstractFastaRecordBuilder<AminoAcid, ProteinSequence, ProteinFastaRecord, ProteinFastaRecordBuilder>{
	/**
	 * Convenience constructor that converts a String into
	 * a {@link ProteinSequence}.  This is the same
	 * as {@link #ProteinFastaRecordBuilder(String, ProteinSequence)
	 * new ProteinSequenceBuilder(id, new ProteinSequenceBuilder(sequence).build())}.
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the amino acid sequence as a string.  May contain whitespace
	 * which will get removed. can not be null.
	 * @throws IllegalArgumentException if any non-whitespace
     * in character in the sequence can not be converted
     * into a {@link AminoAcid}.
     * @throws NullPointerException if either id or sequence are null.
     * @see ProteinSequenceBuilder
	 */
	public ProteinFastaRecordBuilder(String id,
			String sequence) {
		this(id, new ProteinSequenceBuilder(sequence).build());
	}
	/**
	 * Create a new {@link ProteinFastaRecordBuilder}
	 * instance that has the given id and sequence.  
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the sequence of the fasta record; can not be null.
	 *
	 * @throws NullPointerException if either id or sequence are null.
	 */
	public ProteinFastaRecordBuilder(String id,
			ProteinSequence sequence) {
		super(id, sequence);
	}

	@Override
	protected ProteinFastaRecord createNewInstance(String id,
			ProteinSequence sequence, String optionalComment) {
		if(optionalComment==null){
			return new UnCommentedProteinFastaRecord(id, sequence);
		}
		return new CommentedProteinFastaRecord(id, sequence,optionalComment);
	
	}
    @Override
    protected ProteinFastaRecordBuilder getThis() {
        return this;
    }
    @Override
    protected ProteinFastaRecordBuilder newBuilder(String id,
            ProteinSequence seq, String comment) {
        return new ProteinFastaRecordBuilder(id, seq).comment(comment);
    }
}
