package org.jcvi.jillion.internal.fasta;

import org.jcvi.jillion.fasta.FastaRecordVisitor;
/**
 * {@code AbstractResuseableFastaRecordVisitor}
 * is a {@link FastaRecordVisitor}
 * that gathers consecutive calls to
 * {@link #visitBodyLine(String)} to compile the entire
 * body of a fasta record.  This class can 
 * be reused by resetting the current id and comment
 * using {@link #prepareNewRecord(String, String)}
 * so we don't create new instances for each
 * fasta record to be visited. 
 * 
 * @author dkatzel
 *
 */
public abstract class AbstractResuseableFastaRecordVisitor implements FastaRecordVisitor{
	private String currentId;
	private String currentComment;
	private StringBuilder builder;
	
	public final void prepareNewRecord(String id, String optionalComment){
		this.currentId = id;
		this.currentComment = optionalComment;
		builder = new StringBuilder();
	}
	@Override
	public final void visitBodyLine(String line) {
		builder.append(line);
		
	}

	@Override
	public final void visitEnd() {
		visitRecord(currentId, currentComment, builder.toString());
		
	}
    
	public abstract void visitRecord(String id, String optionalComment, String fullBody);
}

