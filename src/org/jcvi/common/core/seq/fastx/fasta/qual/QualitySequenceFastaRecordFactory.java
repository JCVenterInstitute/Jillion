package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.util.Scanner;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
/**
 * {@code QualitySequenceFastaRecordFactory} is a factory class
 * that makes instances of {@link QualitySequenceFastaRecord}s.
 * Depending on the different parameters, the factory might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class QualitySequenceFastaRecordFactory {

	private QualitySequenceFastaRecordFactory(){
		//can not instantiate
	}
	/**
     * Create a {@link QualitySequenceFastaRecord} with the given id, QualitySequence
     * and no comment.  This should create the same {@link QualitySequenceFastaRecord}
     * as {@link #create(String, QualitySequence, String) create(id,sequence, null)}.
     * @param id the id of the fasta record; can not be null.
     * @param sequence the QualitySequence for this record; can not be null.
     * 
     * @return a new QualitySequenceFastaRecord; never null.
     * @throws NullPointerException if either id or entireRecordBody are null.
     */
	public static QualitySequenceFastaRecord create(String id, QualitySequence sequence){
		return new UncommentedQualitySequenceFastaRecord(id, sequence);
	}
	/**
     * Create a {@link QualitySequenceFastaRecord} with the given id, QualitySequence
     * and optional comment.
     * @param id the id of the fasta record; can not be null.
     * @param sequence the QualitySequence for this record; can not be null.
     * @param comment any comments for this record (may be null).
     * 
     * @return a new QualitySequenceFastaRecord; never null.
     * @throws NullPointerException if either id or entireRecordBody are null.
     */
	public static QualitySequenceFastaRecord create(String id, QualitySequence sequence, String comment){
		if(comment==null){
			return create(id,sequence);
		}
		return new CommentedQualitySequenceFastaRecord(id, sequence, comment);
	}
	 /**
     * Create a {@link QualitySequenceFastaRecord} with the given id, comments and
     * formatted record body(which may contain whitespace).
     * Any whitespace in the record body is ignored and the quality values
     * are parsed from the integer values.
     * @param id the id of the fasta record; can not be null.
     * @param entireRecordBody the body of the quality fasta record,
     *  may contain whitespace; can not be null.
     * @param comment any comments for this record (may be null).
     * 
     * @return a new QualitySequenceFastaRecord; never null.
     * @throws NullPointerException if either id or entireRecordBody are null.
     */
	public static QualitySequenceFastaRecord create(
            String id, String entireRecordBody, String comment) {
    	QualitySequence qualitySequence = parseQualitySequence(entireRecordBody);
		return create(id, qualitySequence,comment);
    }
	
	private static QualitySequence parseQualitySequence(String sequence) {
		Scanner scanner = new Scanner(sequence);
    	QualitySequenceBuilder builder = new QualitySequenceBuilder();
    	while(scanner.hasNextByte()){
    		builder.append(scanner.nextByte());
    	}
    	scanner.close();
    	return builder.build();
	}
}
