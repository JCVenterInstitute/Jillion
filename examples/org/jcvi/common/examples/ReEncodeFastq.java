/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.jcvi.jillion.trace.fastq.FastqWriter;
import org.jcvi.jillion.trace.fastq.FastqWriterBuilder;

public class ReEncodeFastq {

	/**
	 * @param args
	 * @throws DataStoreException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DataStoreException, IOException {
		File outFile = new File("out.fastq");
		File fastqFile = new File("path/to/fastq");
		Set<String> idsToInclude = new HashSet<String>();//put names here
		
		DataStoreFilter filter = DataStoreFilters.newIncludeFilter(idsToInclude);
		//for an example, we will tell the parser that
		//this fastqFile has sanger encoded quality values
		//but other factory methods can auto-detect the quality encoding
		//for us for a minor performance penalty.
		FastqDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
										.hint(DataStoreProviderHint.ITERATION_ONLY)
										.qualityCodec(FastqQualityCodec.SANGER)
										.filter(filter)
										.filter(id -> idsToInclude.contains(id))
										.filter(idsToInclude::contains)
										.build();
		
		//note that we are re-encoding it in illumina format
		FastqWriter writer = new FastqWriterBuilder(outFile)
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
