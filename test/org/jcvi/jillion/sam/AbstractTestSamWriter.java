package org.jcvi.jillion.sam;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.sam.header.SamHeader;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class AbstractTestSamWriter {

	public static final class SamDataCollector implements SamVisitor {
		private final List<SamRecord> records = new ArrayList<SamRecord>();
		private SamHeader header;
		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record) {
			records.add(record);
		}

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
				VirtualFileOffset end) {
			records.add(record);
		}

		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			this.header = header;
		}

		@Override
		public void halted() {
			//no-op			
		}

		@Override
		public void visitEnd() {
			//no-op
		}

		public List<SamRecord> getRecords() {
			return records;
		}

		public SamHeader getHeader() {
			return header;
		}
		
		
	}

	private static List<SamRecord> RECORDS = new ArrayList<SamRecord>();
	private static SamHeader HEADER = null;
	
	@BeforeClass
	public static void parseSam() throws IOException{
		ResourceHelper resources = new ResourceHelper(AbstractTestSamWriter.class);
		File samFile = resources.getFile("example.sam");
		
		SamDataCollector collector = new SamDataCollector();
		parseFile(samFile, collector);
		
		RECORDS = collector.getRecords();
		HEADER = collector.getHeader();
	}

	protected static void parseFile(File samFile, SamVisitor visitor)
			throws IOException {
		SamParserFactory.create(samFile).accept(visitor);
	}
	
	@AfterClass
	public static void clearData(){
		RECORDS = null;
		HEADER = null;
		
	}
	
	protected List<SamRecord> getRecords(){
		return new ArrayList<SamRecord>(RECORDS);
	}
	
	protected SamHeader getHeader(){
		return HEADER;
	}

	protected void orderOfRecordsMatchesExactly(File f,
			List<SamRecord> expectedRecords) throws IOException {
		orderOfRecordsMatchesExactly(f, expectedRecords, null);
	}
	protected void orderOfRecordsMatchesExactly(File f,
			List<SamRecord> expectedRecords,
			SortOrder expectedSortOrderInHeader) throws IOException {
		SamDataCollector collector = new SamDataCollector();
		
		parseFile(f, collector);
		SamHeader actualHeader = collector.getHeader();
		
		assertEquals(expectedRecords, collector.getRecords());
		if(expectedSortOrderInHeader !=null){
			SamHeader alteredHeader = new SamHeader.Builder(getHeader())
											.setSortOrder(expectedSortOrderInHeader)
											.build();
			assertEquals(alteredHeader, actualHeader);
		}else{
			assertEquals(getHeader(), actualHeader);
		}
	}

}
