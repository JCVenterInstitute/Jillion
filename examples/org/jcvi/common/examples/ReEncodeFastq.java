package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.datastore.IncludeDataStoreFilter;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecordWriterBuilder;
import org.jcvi.common.core.seq.fastx.fastq.FastqDataStore;
import org.jcvi.common.core.seq.fastx.fastq.FastqFileDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecordWriter;
import org.jcvi.common.core.util.iter.StreamingIterator;

public class ReEncodeFastq {

	/**
	 * @param args
	 * @throws DataStoreException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DataStoreException, IOException {
		File outFile = new File("out.fastq");
		File fastqFile = new File("path/to/fastq");
		List<String> idsToInclude = new ArrayList<String>();//put names here
		
		DataStoreFilter filter = new IncludeDataStoreFilter(idsToInclude);
		//for an example, we will tell the parser that
		//this fastqFile has sanger encoded quality values
		//but other factory methods can auto-detect the quality encoding
		//for us for a minor performance penalty.
		FastqDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
										.hint(DataStoreProviderHint.OPTIMIZE_ITERATION)
										.qualityCodec(FastqQualityCodec.SANGER)
										.filter(filter)
										.build();
		
		//note that we are re-encoding it in illumina format
		FastqRecordWriter writer = new FastqRecordWriterBuilder(outFile)
										.qualityCodec(FastqQualityCodec.ILLUMINA)
										.build();
		
		StreamingIterator<FastqRecord> iter=null;
		try{
			iter = datastore.iterator();
			while(iter.hasNext()){
				FastqRecord fastq = iter.next();
				writer.write(fastq);
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter, writer);
		}

	}

}
