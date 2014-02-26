package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.header.SamHeader;

public class SamWriterBuilder {

	
	private static final int DEFAULT_RECORDS_IN_MEMORY = 2000000; //2 million
	
	private final File outputFile;
	private final SamHeader.Builder headerBuilder;
	private SamAttributeValidator attributeValidator = ReservedAttributeValidator.INSTANCE;
	
	private File tmpDirRoot = null; //default to systemp temp
	
	private SortOrder reSortOrder = null;
	
	private int maxRecordsToKeepInMemory = DEFAULT_RECORDS_IN_MEMORY;
	
	
	public static int getDefaultRecordsToKeepInMemory() {
		return DEFAULT_RECORDS_IN_MEMORY;
	}

	public SamWriterBuilder(File outputFile, SamHeader header){
		if(outputFile == null){
			throw new NullPointerException("output file can not be null");
		}
		if(header ==null){
			throw new NullPointerException("headerBuilder can not be null");
		}
		
		this.outputFile = outputFile;
		this.headerBuilder = new SamHeader.Builder(header);
	}
	
	public SamWriterBuilder setTempRootDir(File tmpDir){
		this.tmpDirRoot = tmpDir;
		return this;
	}
	public SamWriterBuilder setSamAttributeValidator(SamAttributeValidator validator){
		if(validator ==null){
			throw new NullPointerException("validator can not be null");
		}
		this.attributeValidator = validator;
		return this;
	}
	
	public SamWriterBuilder reSortBy(SortOrder sortOrder){
		return reSortBy(sortOrder, DEFAULT_RECORDS_IN_MEMORY);
	}
	public SamWriterBuilder reSortBy(SortOrder sortOrder, int maxRecordsToKeepInMemory){
		if(sortOrder ==null){
			throw new NullPointerException("sort order can not be null");
		}
		if(maxRecordsToKeepInMemory < 0){
			throw new IllegalArgumentException("max records to keep in memory must be positive");
		}
		
		forceHeaderSortOrder(sortOrder);
		this.reSortOrder = sortOrder;
		this.maxRecordsToKeepInMemory = maxRecordsToKeepInMemory;
		return this;
	}
	
	public SamWriterBuilder forceHeaderSortOrder(SortOrder sortOrder){
		headerBuilder.setSortOrder(sortOrder);
		return this;
	}
	
	public SamWriter build() throws IOException{
		SamHeader header = headerBuilder.build();
		if(isBamFile(outputFile)){
			if(writeUnSortedRecords()){
				//no resorting needed
				return new PresortedBamFileWriter(header, outputFile, attributeValidator);
			}
			return new ReSortBamFileWriter(outputFile, tmpDirRoot,header, maxRecordsToKeepInMemory, attributeValidator);
		}else{
			//assume SAM
			if(writeUnSortedRecords()){
				return new PresortedSamFileWriter(outputFile, header, attributeValidator);
			}
			return new ReSortSamFileWriter(outputFile, tmpDirRoot,header, maxRecordsToKeepInMemory, attributeValidator);
		}
	}

	private boolean writeUnSortedRecords() {
		return reSortOrder ==null || reSortOrder == SortOrder.UNKNOWN || reSortOrder == SortOrder.UNSORTED;
	}

	private boolean isBamFile(File f) {
		return "bam".equalsIgnoreCase(FileUtil.getExtension(f));
	}
	
	
}
