/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordWriter;
import org.jcvi.jillion.trace.fastq.FastqRecordWriterBuilder;

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
		
		DataStoreFilter filter = DataStoreFilters.newIncludeFilter(idsToInclude);
		//for an example, we will tell the parser that
		//this fastqFile has sanger encoded quality values
		//but other factory methods can auto-detect the quality encoding
		//for us for a minor performance penalty.
		FastqDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
										.hint(DataStoreProviderHint.ITERATION_ONLY)
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
