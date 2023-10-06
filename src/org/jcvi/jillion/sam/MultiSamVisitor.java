package org.jcvi.jillion.sam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback.SamVisitorMemento;
import org.jcvi.jillion.sam.header.SamHeader;

public class MultiSamVisitor implements SamVisitor{

	private List<SamVisitor> delegates = new ArrayList<>();
	
	public static SamVisitor of(SamVisitor... visitors) {
		return new MultiSamVisitor(Arrays.asList(visitors));
	}
	public static SamVisitor of(Collection<SamVisitor> visitors) {
		return new MultiSamVisitor(new ArrayList<>(visitors));
	}
	private MultiSamVisitor(List<SamVisitor> visitors) {
		this.delegates= visitors;
		visitors.forEach(Objects::requireNonNull);
	}
	
	private static class MultiSamVisitorCallbackWrapper implements SamVisitorCallback{
		private final SamVisitorCallback delegate;

		private SamVisitorMemento createdMemento=null;
		private boolean calledHalt=false;
		
		public MultiSamVisitorCallbackWrapper(SamVisitorCallback delegate) {
			this.delegate = delegate;
		}

		@Override
		public boolean canCreateMemento() {
			return delegate.canCreateMemento();
		}

		@Override
		public SamVisitorMemento createMemento() {
			if(createdMemento==null) {
				createdMemento = delegate.createMemento();
			}
			return createdMemento;
		}

		@Override
		public void haltParsing() {
			if(!calledHalt) {
				calledHalt=true;
				delegate.haltParsing();
			}
			
		}
		
		
	}
	@Override
	public void visitHeader(SamVisitorCallback callback, SamHeader header) {
		MultiSamVisitorCallbackWrapper wrappedCallback = new MultiSamVisitorCallbackWrapper(callback);
		
		delegates.forEach(d -> d.visitHeader(wrappedCallback, header));
	}

	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
			VirtualFileOffset end) {

		MultiSamVisitorCallbackWrapper wrappedCallback = new MultiSamVisitorCallbackWrapper(callback);
		
		delegates.forEach(d -> d.visitRecord(wrappedCallback, record, start, end));
		
	}

	@Override
	public void visitEnd() {
		delegates.forEach(SamVisitor::visitEnd);
		
	}

	@Override
	public void halted() {
		delegates.forEach(SamVisitor::halted);
		
	}

}
