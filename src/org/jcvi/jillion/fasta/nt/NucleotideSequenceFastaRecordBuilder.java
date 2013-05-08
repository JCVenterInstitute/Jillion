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
package org.jcvi.jillion.fasta.nt;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.fasta.AbstractFastaRecordBuilder;
/**
 * {@code NucleotideSequenceFastaRecordBuilder} is a builder class
 * that makes instances of {@link NucleotideSequenceFastaRecord}s.
 * Depending on the different parameters, this builder might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class NucleotideSequenceFastaRecordBuilder extends AbstractFastaRecordBuilder<Nucleotide, NucleotideSequence, NucleotideFastaRecord>{
	/**
	 * Create a new {@link NucleotideSequenceFastaRecordBuilder}
	 * instance that has the given id and sequence.  
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the sequence of the fasta record; can not be null.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	public NucleotideSequenceFastaRecordBuilder(String id,
			NucleotideSequence sequence) {
		super(id, sequence);
	}
	/**
	 * Convenience constructor that converts a String into
	 * a {@link NucleotideSequence}.  This is the same
	 * as {@link #NucleotideSequenceFastaRecordBuilder(String, NucleotideSequence)
	 * new NucleotideSequenceFastaRecordBuilder(id, new NucleotideSequenceBuilder(sequence).build())}.
	 * @param id the id of the fasta record can not be null.
	 * @param sequence the nucleotide sequence as a string.  May contain whitespace
	 * which will get removed. can not be null.
	 * @throws IllegalArgumentException if any non-whitespace
     * in character in the sequence can not be converted
     * into a {@link Nucleotide}.
     * @throws NullPointerException if either id or sequence are null.
     * @see NucleotideSequenceBuilder
	 */
	public NucleotideSequenceFastaRecordBuilder(String id,
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


	
}
