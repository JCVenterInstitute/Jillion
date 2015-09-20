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
		
		long lengthThreshold = 50; // or whatever size you want
		
		
		try(FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
							.hint(DataStoreProviderHint.ITERATION_ONLY)
							.filterRecords(fastq -> fastq.getLength() >= lengthThreshold)
							.build();
				
			FastqWriter writer  = new FastqWriterBuilder(outputFile)
			                                //use same codec as input which was autoDetected
							.qualityCodec(datastore.getQualityCodec())
							.build();
			StreamingIterator<FastqRecord> iter = datastore.iterator();
		){
			while(iter.hasNext()){
			    //our datastore filtered out anything 
			    //that didn't meet our length requirements 
			    //so it's safe to write everything in the iterator
			    writer.write(iter.next());
			}
		}
		
	}

}
