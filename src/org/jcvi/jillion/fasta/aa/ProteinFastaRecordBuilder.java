/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.internal.fasta.AbstractFastaRecordBuilder;
import org.jcvi.jillion.internal.fasta.aa.CommentedProteinFastaRecord;
import org.jcvi.jillion.internal.fasta.aa.UnCommentedProteinFastaRecord;
/**
 * {@code ProteinFastaRecordBuilder} is a Builder class
 * that makes instances of {@link AminoAcidSequenceFastaRecord}s.
 * Depending on the different parameters, this builder might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class ProteinFastaRecordBuilder extends AbstractFastaRecordBuilder<AminoAcid, ProteinSequence, ProteinFastaRecord>{
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
}
