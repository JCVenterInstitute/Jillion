package org.jcvi.jillion.sam;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.SlowTests;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamReferenceSequence;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@Category(SlowTests.class)
@RunWith(Parameterized.class)
public class TestBamParserImplementations {

	private static final SamParser FULL_BAM_PARSER;
	
	private static ResourceHelper RESOURCES = new ResourceHelper(TestBamParserImplementations.class);
	
	
	static{
		
		try {
			FULL_BAM_PARSER = SamParserFactory.create(RESOURCES.getFile("copy.wgEncodeUwRepliSeqK562G1AlnRep1.bam"));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Parameters
	public static List<Object[]> getData(){
		
		Supplier<SamParser> notIndexed = ()->{
			try{
				return SamParserFactory.create(RESOURCES.getFile("copy.wgEncodeUwRepliSeqK562G1AlnRep1.bam"));
			}catch(IOException e){
				throw new UncheckedIOException(e);
			}};
			
		Supplier<SamParser> implicitlyIndexed = ()->{
				try{
					return SamParserFactory.create(RESOURCES.getFile("wgEncodeUwRepliSeqK562G1AlnRep1.bam"));
				}catch(IOException e){
					throw new UncheckedIOException(e);
				}};
		Supplier<SamParser> explicitlyIndexed = ()->{
					try{
						return SamParserFactory.createFromBamIndex(
								RESOURCES.getFile("wgEncodeUwRepliSeqK562G1AlnRep1.bam"),
								RESOURCES.getFile("wgEncodeUwRepliSeqK562G1AlnRep1.bam.bai")
								);
					}catch(IOException e){
						throw new UncheckedIOException(e);
					}};
				
			
		return Arrays.asList(new Object[]{ notIndexed},
				new Object[]{implicitlyIndexed},
				new Object[]{explicitlyIndexed});
	}
	
	
	private final SamParser actualParser;


	public TestBamParserImplementations(Supplier<SamParser> actualParserSupplier) {
		this.actualParser = actualParserSupplier.get();
	}
	
	@Test
	public void fromAll() throws IOException{
		ReplayableMockSamVisitor mock = new ReplayableMockSamVisitor(true);
		FULL_BAM_PARSER.accept(mock);
		
		actualParser.accept(mock);
	}
	
	@Test
	public void firstReferenceOnlyBeginningOfAlignment() throws IOException{
		Range alignmentRange = Range.ofLength(2000);
		ReplayableMockSamVisitor mock = new ReplayableMockSamVisitor(true);
		String firstReferenceName = FULL_BAM_PARSER.getHeader().getReferenceSequences().iterator().next().getName();
		
		FULL_BAM_PARSER.accept(new WrappedVisitor(mock){

			@Override
			protected boolean accept(SamRecord record) {
				if(! record.mapped() || !firstReferenceName.equals(record.getReferenceName())){
					return false;
				}
				
				Range readAlignmentRange = record.getAlignmentRange();
				return readAlignmentRange.isSubRangeOf(alignmentRange);
			}
			
		});
		
		actualParser.accept(firstReferenceName, alignmentRange, mock);
	}
	
	@Test
	public void firstReferenceOnly() throws IOException{
		ReplayableMockSamVisitor mock = new ReplayableMockSamVisitor(true);
		String firstReferenceName = FULL_BAM_PARSER.getHeader().getReferenceSequences().iterator().next().getName();
		
		FULL_BAM_PARSER.accept(new WrappedVisitor(mock){

			@Override
			protected boolean accept(SamRecord record) {
				return record.mapped() && firstReferenceName.equals(record.getReferenceName());
			}
			
		});
		
		actualParser.accept(firstReferenceName, mock);
	}
	
	private String getLastReferenceName(SamHeader header){
		Iterator<SamReferenceSequence> iter =header.getReferenceSequences().iterator();
		String ret=null;
		while(iter.hasNext()){
			ret = iter.next().getName();
		}
		return ret;
	}
	
	private String getInnerReferenceName(SamHeader header){
		Iterator<SamReferenceSequence> iter =header.getReferenceSequences().iterator();
		String ret=null;
		
		iter.next();
		while(iter.hasNext()){
			SamReferenceSequence next = iter.next();
			if(iter.hasNext()){
				ret = next.getName();
				
				iter.next();
			}
		}
		return ret;
	}
	
	@Test
	public void lastReferenceOnly() throws IOException{
		ReplayableMockSamVisitor mock = new ReplayableMockSamVisitor(true);
		String lastReferenceName = getLastReferenceName(FULL_BAM_PARSER.getHeader());
		
		FULL_BAM_PARSER.accept(new WrappedVisitor(mock){

			@Override
			protected boolean accept(SamRecord record) {
				return record.mapped() && lastReferenceName.equals(record.getReferenceName());
			}
			
		});
		
		actualParser.accept(lastReferenceName, mock);
	}
	
	@Test
	public void innerReferenceOnly() throws IOException{
		ReplayableMockSamVisitor mock = new ReplayableMockSamVisitor(true);
		String lastReferenceName = getInnerReferenceName(FULL_BAM_PARSER.getHeader());
		
		FULL_BAM_PARSER.accept(new WrappedVisitor(mock){

			@Override
			protected boolean accept(SamRecord record) {
				return record.mapped() && lastReferenceName.equals(record.getReferenceName());
			}
			
		});
		
		actualParser.accept(lastReferenceName, mock);
	}
	
	private static abstract class WrappedVisitor implements SamVisitor{
		private final SamVisitor delegate;

		public WrappedVisitor(SamVisitor delegate) {
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
}



