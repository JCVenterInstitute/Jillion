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

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.fastq.FastqFileDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqWriter;
import org.jcvi.jillion.trace.fastq.FastqWriterBuilder;
import org.jcvi.jillion.trim.BwaQualityTrimmer;

public class TrimFastq {

	public static void main(String[] args) throws IOException, DataStoreException {
		File fastqFile = new File("/path/to/input.fasta");
		File outputFile = new File("/path/to/output.fasta");
		
		long minLength = 30; // or whatever size you want
		
		BwaQualityTrimmer bwaTrimmer = new BwaQualityTrimmer(PhredQuality.valueOf(20));
		
		try(FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
							.hint(DataStoreProviderHint.ITERATION_ONLY)
							.filterRecords(fastq -> fastq.getLength() >= minLength)
							.build();
				
			FastqWriter writer  = new FastqWriterBuilder(outputFile)
			                                //use same codec as input which was autoDetected
							.qualityCodec(datastore.getQualityCodec())
							.build();
			ThrowingStream<FastqRecord> stream = datastore.records();
		){
			//our datastore filtered out anything 
                        //that didn't meet our length requirements 
                        //so it's safe to write everything in the stream.
		        //uses Jillion's custom ThrowingStream which has methods
		        //that can throw exceptions since our writer will throw an IOException
			stream.throwingForEach(fastq -> {
                            Range trimRange = bwaTrimmer.trim(fastq.getQualitySequence());
                            if (trimRange.getLength() >= minLength) {
                                writer.write(fastq);
                            }			
			});
			
		}//streams and datastores will autoclose when end of scope reached.
		
	}

}
