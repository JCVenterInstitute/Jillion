package org.jcvi.jillion.fasta.qual;

import org.jcvi.jillion.fasta.FastaRecordVisitor;
/**
 * {@code AbstractQualityFastaRecordVisitor} is an abstract
 * implementation of {@link FastaRecordVisitor} that will collect
 * the visit methods <strong>for a single fasta record</strong>
 * and build an instance of {@link QualitySequenceFastaRecord}.
 * When {@link FastaRecordVisitor#visitEnd()} is called,
 * the {@link QualitySequenceFastaRecord} is built
 * and the abstract method {@link #visitRecord(QualitySequenceFastaRecord)}
 * will be called.  
 * 
 * <p/>
 * A new instance of this class should be used for each fasta record
 * to be visited.  This class is not threadsafe.
 * @author dkatzel
 *
 */
public abstract class AbstractQualityFastaRecordVisitor  implements FastaRecordVisitor{
	
	private final String id;
	private final String comment;
	private final StringBuilder sequenceBuilder = new StringBuilder();
	
	
	public AbstractQualityFastaRecordVisitor(String id, String comment) {
		this.id = id;
		this.comment = comment;
	}

	@Override
	public final void visitBodyLine(String line) {
		sequenceBuilder.append(line);
		
	}

	@Override
	public final void visitEnd() {
		QualitySequenceFastaRecord record = new QualitySequenceFastaRecordBuilder(id, sequenceBuilder.toString())
												.comment(comment)
												.build();
		visitRecord(record);
		
	}
	
	protected abstract void visitRecord(QualitySequenceFastaRecord fastaRecord);
	

}
