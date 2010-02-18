/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;
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
