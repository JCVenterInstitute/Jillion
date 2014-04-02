package org.jcvi.jillion.sam.index;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.sam.BamIndexer;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;
import org.junit.Test;

public class TestBamIndexParser {

	ResourceHelper resources = new ResourceHelper(TestBamIndexParser.class);
	
	@Test
	public void parsedBaiMatchesRecordsInBam() throws IOException{
		BamIndexSamVisitor visitor = createIndexFromBam(resources.getFile("index_test.bam"));
		InputStream in =null;
		try{
			in= new BufferedInputStream(resources.getFileAsStream("index_test.bam.bai"));
			List<ReferenceIndex> expectedIndex = IndexUtil.parseIndex(in, visitor.getHeader());
			List<ReferenceIndex> actualIndex = visitor.getIndexes();
			//even though assertEquals(list, list) 
			//would work if the assertion fails, 
			//the stack trace is so big (mostly error mesage)
			//it causes out of mem exception
			//and takes forever to run.
			//so we break up the checks to speed things up.
			assertEquals("num indexes different",  actualIndex.size(), expectedIndex.size());
			
			Iterator<ReferenceIndex> actualIter = actualIndex.iterator();
			Iterator<ReferenceIndex> expectedIter = expectedIndex.iterator();
			int i=0;
			while(expectedIter.hasNext()){
				ReferenceIndex expectedRefIndex = expectedIter.next();
				ReferenceIndex actualRefIndex = actualIter.next();
				assertBinsMatch(expectedRefIndex, actualRefIndex, i);
				
				assertArrayEquals("intervals for " +i, expectedRefIndex.getIntervals(), actualRefIndex.getIntervals());
				i++;
			}
			assertFalse(actualIter.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
		
	}
	
	private void assertBinsMatch(ReferenceIndex expected, ReferenceIndex actual, int refCount){
		List<Bin> expectedBins = expected.getBins();
		List<Bin> actualBins = actual.getBins();
		
		assertEquals("bin size " +refCount, expectedBins.size(), actualBins.size());
		assertEquals(expectedBins, actualBins);
		
	}

	private BamIndexSamVisitor createIndexFromBam(File bam) throws IOException {
		BamIndexSamVisitor visitor = new BamIndexSamVisitor();
		SamParserFactory.create(bam).accept(visitor);
		return visitor;
	}
	
	private static class BamIndexSamVisitor implements SamVisitor {
		BamIndexer indexer;
		SamHeader header;
		
		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record,
				VirtualFileOffset start, VirtualFileOffset end) {
			indexer.addRecord(record, start, end);
			
		}
		
		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			indexer = new BamIndexer(header);
			this.header = header;
		}
		
		@Override
		public void visitEnd() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void halted() {
			// TODO Auto-generated method stub
			
		}

		public List<ReferenceIndex> getIndexes() {
			return indexer.createReferenceIndexes();
		}

		public SamHeader getHeader() {
			return header;
		}
		
		
	}
}
