/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.align;

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
