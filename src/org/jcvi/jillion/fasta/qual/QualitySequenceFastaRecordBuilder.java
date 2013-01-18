package org.jcvi.jillion.fasta.qual;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.internal.fasta.AbstractFastaRecordBuilder;
/**
 * {@code QualitySequenceFastaRecordBuilder} is a factory class
 * that makes instances of {@link QualitySequenceFastaRecord}s.
 * Depending on the different parameters, the factory might
 * choose to return different implementations.
 * @author dkatzel
 *
 */
public final class QualitySequenceFastaRecordBuilder extends AbstractFastaRecordBuilder<PhredQuality, QualitySequence,QualitySequenceFastaRecord> {
	/**
	 * Pattern used to pull out individual quality
	 * values from the body of a string (or fasta file).
	 */
	private static final Pattern QUAL_BODY_PATTERN = Pattern.compile("\\d+");
	/**
	 * Create a new builder instance for the given id and 
	 * entire quality values as a human readable string.
	 * @param id the id of the quality fasta record; can not be null.
	 * @param entireRecordBody a string representation of the quality sequence,
     *  must contain whitespace to separate quality values; each quality value
     *  may have leading
     *  zeros but can not be null.
     *  @throws NullPointerException if either parameter is null.
	 */
	public QualitySequenceFastaRecordBuilder(String id, String entireRecordBody){
		this(id,parseQualitySequence(entireRecordBody));
	}
	/**
	 * Create a new builder instance for the given id and {@link QualitySequence}.
	 * @param id the id of the quality fasta record; can not be null.
	 * @param sequence the quality values of this 
	 *  @throws NullPointerException if either parameter is null.
	 */
	
	private static QualitySequence parseQualitySequence(String sequence) {
		QualitySequenceBuilder builder = new QualitySequenceBuilder(sequence.length());
		
		Matcher m = QUAL_BODY_PATTERN.matcher(sequence);
		while(m.find()){
			builder.append(Byte.parseByte(m.group()));
		}
		
    	return builder.build();
	}
	public QualitySequenceFastaRecordBuilder(String id, QualitySequence sequence) {
		super(id, sequence);
	}
	@Override
	protected QualitySequenceFastaRecord createNewInstance(String id, QualitySequence sequence,
			String comment) {
		if(comment ==null){
			return new UncommentedQualitySequenceFastaRecord(id, sequence);
		}
		return new CommentedQualitySequenceFastaRecord(id, sequence, comment);
	}
	
	
}
