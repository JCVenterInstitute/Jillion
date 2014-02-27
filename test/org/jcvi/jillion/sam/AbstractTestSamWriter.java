package org.jcvi.jillion.sam;

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
		public void visitRecord(SamRecord record) {
			records.add(record);
		}

		@Override
		public void visitHeader(SamHeader header) {
			this.header = header;
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



}
