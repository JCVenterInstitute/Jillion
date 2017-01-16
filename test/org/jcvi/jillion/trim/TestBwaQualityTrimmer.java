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
package org.jcvi.jillion.trim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.junit.Test;
public class TestBwaQualityTrimmer {

	private final FastqDataStore inputFastq, outputFastq;
	private final BwaQualityTrimmer sut = new BwaQualityTrimmer(PhredQuality.valueOf(20));
	public TestBwaQualityTrimmer() throws FileNotFoundException, IOException{
		ResourceHelper resources = new ResourceHelper(TestBwaQualityTrimmer.class);
		inputFastq = new FastqFileDataStoreBuilder(resources.getFile("files/bwa_input.fastq"))
							.hint(DataStoreProviderHint.ITERATION_ONLY)
							.qualityCodec(FastqQualityCodec.SANGER)
							.build();
			
		outputFastq = new FastqFileDataStoreBuilder(resources.getFile("files/bwa_output.fastq"))
							.hint(DataStoreProviderHint.ITERATION_ONLY)
							.qualityCodec(FastqQualityCodec.SANGER)
							.build();
		
	}
	@Test
	public void trimsCorrectly() throws DataStoreException{
		//the output file was created using a 64 bp min
		//so anything <64 should get automatically trimmed:
		StreamingIterator<FastqRecord> inputIterator =null;
		StreamingIterator<FastqRecord> expectedIterator =null;
		try{
			inputIterator = inputFastq.iterator();
			expectedIterator = outputFastq.iterator();
			while(inputIterator.hasNext()){
				FastqRecord input = inputIterator.next();
				Range trimmedRange = sut.trim(input.getQualitySequence());
				if(trimmedRange.getLength() >=64){
					FastqRecord trimmedSequence = input.toBuilder()
					                                   .trim(trimmedRange)
					                                   .build();
					    
					assertEquals(trimmedSequence.getId(),expectedIterator.next(), trimmedSequence);
				}
			}
			assertFalse(expectedIterator.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(inputIterator,expectedIterator);
		}
	}
	
}
