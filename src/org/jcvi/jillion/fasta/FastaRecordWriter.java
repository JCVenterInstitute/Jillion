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
package org.jcvi.jillion.fasta;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.jillion.core.Sequence;
/**
 * {@code FastaRecordWriter} is a interface
 * that handles how {@link FastaRecord}s
 * are written.
 * @author dkatzel
 *
 */
public interface FastaRecordWriter<S, T extends Sequence<S>, F extends FastaRecord<S, T>> extends Closeable{
	/**
	 * Write the given {@link FastaRecord}
	 * (including the optionalComment if there is one).
	 * @param record the {@link FastaRecord}
	 * to write, can not be null.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if record is null.
	 */
	void write(F record) throws IOException;
	/**
	 * Write the given id and {@link Sequence}
	 * out as a {@link FastaRecord} without a comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link Sequence} to be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	void write(String id, T sequence) throws IOException;
	/**
	 * Write the given id and {@link Sequence}
	 * out as a {@link FastaRecord} without a comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link Sequence} to be written.
	 * @param optionalComment comment to write, if this value is null,
	 * then no comment will be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id or sequence are null.
	 */
	void write(String id, T sequence, String optionalComment) throws IOException;
}
