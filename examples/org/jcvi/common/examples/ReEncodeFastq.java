package org.jcvi.common.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.IncludeFastXIdFilter;
import org.jcvi.common.core.seq.fastx.fastq.FastqDataStore;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecord;
import org.jcvi.common.core.seq.fastx.fastq.LargeFastqFileDataStore;
import org.jcvi.common.core.util.iter.StreamingIterator;

public class ReEncodeFastq {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws DataStoreException 
	 */
	public static void main(String[] args) throws FileNotFoundException, DataStoreException {
		PrintStream out = new PrintStream("out.fastq");
		File fastqFile = new File("path/to/fastq");
		List<String> idsToInclude = new ArrayList<String>();//put names here
		
		FastXFilter filter = new IncludeFastXIdFilter(idsToInclude);
		//for an example, we will tell the parser that
		//this fastqFile has sanger encoded quality values
		//but other factory methods can auto-detect the quality encoding
		//for us for a minor performance penalty.
		FastqDataStore datastore = LargeFastqFileDataStore.create(fastqFile, 
												filter, FastqQualityCodec.SANGER);
		StreamingIterator<FastqRecord> iter=null;
		try{
			iter = datastore.iterator();
			while(iter.hasNext()){
				FastqRecord fastq = iter.next();
				//here we are re-encoding it in illumina format!
				out.print(fastq.toFormattedString(FastqQualityCodec.ILLUMINA));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter, out);
		}

	}

}
