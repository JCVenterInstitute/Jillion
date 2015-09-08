package org.jcvi.jillion.examples.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.fastq.FastqFileDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqWriter;
import org.jcvi.jillion.trace.fastq.FastqWriterBuilder;

public class FilterFastqRecordBySize {

	public static void main(String[] args) throws IOException, DataStoreException {
		File fastqFile = new File("/path/to/input.fasta");
		File outputFile = new File("/path/to/output.fasta");
		
		final long lengthThreshold = 50; // or whatever size you want
		
		
		try(FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
												.hint(DataStoreProviderHint.ITERATION_ONLY)
												.build();
				
			FastqWriter writer  = new FastqWriterBuilder(outputFile)
											.qualityCodec(datastore.getQualityCodec())
											.build();
			StreamingIterator<FastqRecord> iter = datastore.iterator();
		){
			while(iter.hasNext()){
				FastqRecord record = iter.next();
				if(record.getNucleotideSequence().getLength() < lengthThreshold){
					continue;
				}
				writer.write(record);
			}
		}
		
	}

}
