package org.jcvi.jillion.internal.fasta;

import java.util.Objects;

import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
/**
 * A {@link FastaVisitor} that will only visit at most the given number of records.
 * The record count is only incremented when the delegate visitor's {@link FastaVisitor#visitDefline(FastaVisitorCallback, String, String)}
 * returns non-null.  Once the max record count is reached,
 * this visitor will call {@link FastaVisitorCallback#haltParsing()}.
 * 
 * @author dkatzel
 * 
 * @since 6.0
 *
 */
public class MaxNumberOfRecordsFastaVisitor implements FastaVisitor{

	private final long total;
	private long numberSeen = 0;
	private final FastaVisitor delegate;
	
	
	public MaxNumberOfRecordsFastaVisitor(long total, FastaVisitor delegate) {
		if(total < 1) {
			throw new IllegalArgumentException("total number of records to visit must be >=1");
		}
		this.total = total;
		this.delegate = Objects.requireNonNull(delegate);
	}

	@Override
	public FastaRecordVisitor visitDefline(FastaVisitorCallback callback, String id, String optionalComment) {
		if(numberSeen > total) {
			callback.haltParsing();
			return null;
		}
		FastaRecordVisitor recordVisitor = delegate.visitDefline(callback, id, optionalComment);
		if(recordVisitor !=null) {
			numberSeen++;
		}
		return recordVisitor;
	}

	@Override
	public void visitEnd() {
		delegate.visitEnd();
		
	}

	@Override
	public void halted() {
		delegate.halted();
		
	}

}
