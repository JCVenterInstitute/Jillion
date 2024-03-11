package org.jcvi.jillion.sam;

import java.util.Objects;

import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback;
/**
 * Helper factory methods to make new {@link SamVisitor} 
 * instances by using a functional interface.
 * 
 * For example: instead of 
 * 
 * <pre>
 * {@code
 * SamParserFactory.create(bam).parse(new AbstractSamVisitor() {

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start, VirtualFileOffset end) {
			//...
		}
	});
	}
	</pre>
	you can do:
	
	<pre>
{@code SamParserFactory.create(bam).parse(SamVisitorFunctions.recordVisitor(record-> ... ));}
	</pre>
 * There are multiple overloaded methods with varying parameters.
			
 * @author dkatzel
 * 
 * @since 6.0.2
 *
 */
public final class SamVisitorFunctions {

	private SamVisitorFunctions() {
		//can not instantiate
	}
	@FunctionalInterface
	public static interface RecordVisitorWithCallback{
		void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start, VirtualFileOffset end);
	}
	
	@FunctionalInterface
	public static interface RecordVisitorWithoutCallback{
		void visitRecord(SamRecord record, VirtualFileOffset start, VirtualFileOffset end);
	}
	
	@FunctionalInterface
	public static interface RecordVisitorRecordOnly{
		void visitRecord(SamRecord record);
	}
	
	public static SamVisitor recordVisitor(RecordVisitorWithCallback visitorFunction) {
		Objects.requireNonNull(visitorFunction);
		
		return new AbstractSamVisitor() {

			@Override
			public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
					VirtualFileOffset end) {
				visitorFunction.visitRecord(callback, record, start, end);
			}
			
		};
	}
	
	public static SamVisitor recordVisitor(RecordVisitorWithoutCallback visitorFunction) {
		Objects.requireNonNull(visitorFunction);
		
		return new AbstractSamVisitor() {

			@Override
			public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
					VirtualFileOffset end) {
				visitorFunction.visitRecord(record, start, end);
			}
			
		};
	}
	public static SamVisitor recordVisitor(RecordVisitorRecordOnly visitorFunction) {
		Objects.requireNonNull(visitorFunction);
		
		return new AbstractSamVisitor() {

			@Override
			public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
					VirtualFileOffset end) {
				visitorFunction.visitRecord(record);
			}
			
		};
	}
}
