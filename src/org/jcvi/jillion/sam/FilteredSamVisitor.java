package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback;
import org.jcvi.jillion.sam.header.SamHeader;

public abstract class FilteredSamVisitor implements SamVisitor{
	private final SamVisitor delegate;

	public FilteredSamVisitor(SamVisitor delegate) {
		this.delegate = delegate;
	}
	@Override
	public void visitHeader(SamVisitorCallback callback, SamHeader header) {
		delegate.visitHeader(callback, header);
	}

	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
			VirtualFileOffset end) {
		if(accept(record)){
			delegate.visitRecord(callback, record, start, end);
		}
		
	}

	protected abstract boolean accept(SamRecord record);
	@Override
	public void visitEnd() {
		delegate.visitEnd();
	}

	@Override
	public void halted() {
		delegate.halted();
	}
	
	
}