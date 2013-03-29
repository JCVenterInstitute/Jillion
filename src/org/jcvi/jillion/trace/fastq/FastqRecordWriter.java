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
package org.jcvi.jillion.trace.fastq;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code FastqRecordWriter} is an interface
 * that handles writing out {@link FastqRecord}s.
 * @author dkatzel
 *
 */
public interface FastqRecordWriter extends Closeable{
	/**
	 * Write the given {@link FastqRecord} out.
	 * @param record the {@link FastqRecord} to write;
	 * can not be null.
	 * @throws IOException if there is a problem writing out the
	 * {@link FastqRecord}.
	 * @throws NullPointerException if record is null.
	 */
	void write(FastqRecord record) throws IOException;	
	/**
	 * Write the given id, {@link NucleotideSequence}
	 * and {@link QualitySequence}
	 * out as a {@link FastqRecord} without a comment.
	 * @param id the id of the record to be written.
	 * @param nucleotides the {@link NucleotideSequence} to be written.
	 * @param qualities the {@link QualitySequence} to be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id, nucleotides or qualities are null.
	 */
	void write(String id, NucleotideSequence nucleotides, QualitySequence qualities) throws IOException;
	/**
	 * Write the given id and {{@link NucleotideSequence}
	 * and {@link QualitySequence}
	 * out as a {@link FastqRecord} along with the optional comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link NucleotideSequence} to be written.
	 * @param qualities the {@link QualitySequence} to be written.
	 * @param optionalComment comment to write, if this value is null,
	 * then no comment will be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id, nucleotides or qualities are null.
	 */
	void write(String id, NucleotideSequence sequence, QualitySequence qualities, String optionalComment) throws IOException;
}
