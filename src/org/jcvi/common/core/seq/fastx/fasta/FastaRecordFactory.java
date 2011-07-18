/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta;
/**
 * {@code FastaRecordFactory} is a Factory for creating
 * FastaRecords.
 * @author dkatzel
 *
 *
 */
public interface FastaRecordFactory<T extends FastaRecord> {
    /**
     * Create a FastaRecord with the given id, comments and record body.
     * Any whitespace in the record body is ignored.
     * @param id the id of the fasta record.
     * @param comments any comments for this record (may be null).
     * @param recordBody the body of the fasta record, may contain whitespace.
     * @return a new FastaRecord.
     */
    T createFastaRecord(String id, String comments, String recordBody);
    /**
     * Convenience method for a creating a FastaRecord if there is no comment.
     * This is the same as {@link #createFastaRecord(String, String, String)
     * createFastaRecord(id,null,recordBody)}
     * @param id the id of the fasta record.
     * @param recordBody the body of the fasta record, may contain whitespace.
     * @return a new FastaRecord.
     * @see FastaRecordFactory#createFastaRecord(String, String, String)
     */
    T createFastaRecord(String id,  String recordBody);
}
