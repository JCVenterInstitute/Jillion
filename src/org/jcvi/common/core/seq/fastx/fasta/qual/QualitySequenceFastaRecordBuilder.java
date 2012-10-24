package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.util.Scanner;

import org.jcvi.common.core.seq.fastx.fasta.FastaRecord;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
/**
 * {@code QualitySequenceFastaRecordBuilder} is a factory class
 * that makes instances of {@link QualitySequenceFastaRecord}s.
 * Depending on the different parameters, the factory might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class QualitySequenceFastaRecordBuilder {

	private final String id;
	private final QualitySequence sequence;
	private String comment=null;
	/**
	 * Create a new builder instance for the given id and 
	 * entire quality values as a human readable string.
	 * @param id the id of the quality fasta record; can not be null.
	 * @param entireRecordBody the body of the quality fasta record,
     *  may contain whitespace to separate quality values; can not be null.
     *  @throws NullPointerException if either parameter is null.
	 */
	public QualitySequenceFastaRecordBuilder(String id, String entireRecordBody){
		this(id,parseQualitySequence(entireRecordBody));
	}
	/**
	 * Create a new builder instance for the given id and {@link QualitySequence}.
	 * @param id the id of the quality fasta record; can not be null.
	 * @param sequence the {@link QualitySequence} for this quality fasta record;
	 * can not be null.
	 *  @throws NullPointerException if either parameter is null.
	 */
	public QualitySequenceFastaRecordBuilder(String id, QualitySequence sequence){
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		if(sequence ==null){
			throw new NullPointerException("sequence can not be null");
		}
		this.id = id;
		this.sequence = sequence;
	}
	/**
	 * Add an optional comment to this fasta record.
	 * This will be the value returned by {@link FastaRecord#getComment()}.
	 * Calling this method more than once will cause the last value to
	 * overwrite the previous value.
	 * @param comment the comment for this fasta record;
	 * if this value is null, then there is no comment.
	 * @return this.
	 */
	public QualitySequenceFastaRecordBuilder comment(String comment){
		this.comment = comment;
		return this;
	}
	/**
	 * Create a new instance of {@link QualitySequenceFastaRecord}
	 * using the given parameters so far.
	 * @return
	 */
	public QualitySequenceFastaRecord build(){
		if(comment ==null){
			return new UncommentedQualitySequenceFastaRecord(id, sequence);
		}
		return new CommentedQualitySequenceFastaRecord(id, sequence, comment);
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
