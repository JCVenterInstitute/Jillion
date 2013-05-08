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

import java.io.IOException;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.fasta.FastaRecordWriter;
/**
 * {@code NucleotideFastaRecordWriter} is a interface
 * that handles how {@link NucleotideFastaRecord}s
 * are written.
 * @author dkatzel
 *
 */
public interface NucleotideFastaRecordWriter extends FastaRecordWriter<Nucleotide, NucleotideSequence, NucleotideFastaRecord>{
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
