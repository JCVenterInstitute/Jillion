package org.jcvi.jillion.testutils.assembly.cas;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.fastq.FastqWriterBuilder;

public final class FastqRecordWriter implements RecordWriter{
	
	
	private final org.jcvi.jillion.trace.fastq.FastqWriter writer;
	private final File fastqFile;
	private final int maxNumberOfRecordsToWrite;
	private int counter=0;
	
	
	public FastqRecordWriter(File workingDir) throws IOException{
		this(workingDir, Integer.MAX_VALUE);
	}
	public FastqRecordWriter(File workingDir, int maxRecordsToWrite) throws IOException{
		if(maxRecordsToWrite <1){
			throw new IllegalArgumentException("max records can not < 1");
		}
		fastqFile = File.createTempFile("reads", ".fastq", workingDir);
		writer = new FastqWriterBuilder(fastqFile).build();
		maxNumberOfRecordsToWrite = maxRecordsToWrite;
		
	}
	
	public FastqRecordWriter(File workingDir, String filename, int maxRecordsToWrite) throws IOException{
		if(maxRecordsToWrite <1){
			throw new IllegalArgumentException("max records can not < 1");
		}
		fastqFile = new File(workingDir, filename);
		writer = new FastqWriterBuilder(fastqFile).build();
		maxNumberOfRecordsToWrite = maxRecordsToWrite;
		
	}
	
	@Override
	public void close() throws IOException {
		writer.close();
	}
	@Override
	public void write(String id, NucleotideSequence seq) throws IOException {
		if(!canWriteAnotherRecord()){
			throw new IllegalStateException("too many records to write");
		}
		counter++;
		//fake qualities
		int length = (int)seq.getLength();
		QualitySequence quals;
		if(length ==0){
			quals = new QualitySequenceBuilder(0).build();
		}else{

			QualitySequenceBuilder builder = new QualitySequenceBuilder(length);
			for(int i=0; i<length; i++){
				builder.append(RecordWriter.DEFAULT_QV);
			}
			quals = builder.build();
		}
		
		writer.write(id, seq, quals);
	}
	
	public boolean canWriteAnotherRecord(){
		return counter != maxNumberOfRecordsToWrite;
	}
	
	public File getFile(){
		return fastqFile;
	}
	
	
}