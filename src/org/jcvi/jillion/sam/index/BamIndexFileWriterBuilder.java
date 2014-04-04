package org.jcvi.jillion.sam.index;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.sam.BamIndexer;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;

public class BamIndexFileWriterBuilder {

	
	private boolean includeMetaData=false;
	private File outputBaiFile, inputBamFile;
	
	
	public BamIndexFileWriterBuilder(File inputBamFile, File outputBaiFile) throws IOException{
		if(inputBamFile == null){
			throw new NullPointerException("output bai file can not be null");
		}
		if(outputBaiFile == null){
			throw new NullPointerException("output bai file can not be null");
		}
		if(!FileUtil.getExtension(inputBamFile).equalsIgnoreCase("bam")){
			throw new IllegalArgumentException("input file is not a bam file " + inputBamFile.getAbsolutePath());
		}
		if(!FileUtil.getExtension(outputBaiFile).equalsIgnoreCase("bai")){
			throw new IllegalArgumentException("input file is not a bai file " + outputBaiFile.getAbsolutePath());
		}
		IOUtil.mkdirs(outputBaiFile.getParentFile());
		this.inputBamFile = inputBamFile;
		this.outputBaiFile = outputBaiFile;
		
	}
	
	public BamIndexFileWriterBuilder includeMetaData(boolean includeMetaData){
		this.includeMetaData = includeMetaData;
		return this;
	}
	
	public void build() throws IOException{
		SamParser parser = SamParserFactory.create(inputBamFile);
		BamIndexSamVisitor visitor = new BamIndexSamVisitor();
		parser.accept(visitor);
		
		OutputStream out =null;
		try{
			out = new BufferedOutputStream(new FileOutputStream(outputBaiFile));
			IndexUtil.writeIndex(out, visitor.createBamIndex(), includeMetaData);
		}finally{
			IOUtil.closeAndIgnoreErrors(out);
		}
	}
	
	
	private static class BamIndexSamVisitor implements SamVisitor {
		private BamIndexer indexer;
		

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record,
				VirtualFileOffset start, VirtualFileOffset end) {
			indexer.addRecord(record, start, end);
			
		}
		
		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record) {
			// TODO no-op or throw exception?
			
		}
		
		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			indexer = new BamIndexer(header);
		}
		
		@Override
		public void visitEnd() {
			//no-op
		}
		
		@Override
		public void halted() {
			//no-op
		}
		
		public BamIndex createBamIndex(){
			return indexer.createBamIndex();
		}
		
	}
}
