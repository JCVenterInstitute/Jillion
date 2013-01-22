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
package org.jcvi.jillion.core.qual.trim;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.trim.BwaQualityTrimmer;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordBuilder;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestBwaQualityTrimmer {

	private final FastqDataStore inputFastq, outputFastq;
	private final BwaQualityTrimmer sut = new BwaQualityTrimmer(PhredQuality.valueOf(20));
	public TestBwaQualityTrimmer() throws FileNotFoundException, IOException{
		ResourceHelper resources = new ResourceHelper(TestBwaQualityTrimmer.class);
		inputFastq = new FastqFileDataStoreBuilder(resources.getFile("files/bwa_input.fastq"))
							.hint(DataStoreProviderHint.OPTIMIZE_ITERATION)
							.qualityCodec(FastqQualityCodec.SANGER)
							.build();
			
		outputFastq = new FastqFileDataStoreBuilder(resources.getFile("files/bwa_output.fastq"))
							.hint(DataStoreProviderHint.OPTIMIZE_ITERATION)
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
					FastqRecord trimmedSequence = new FastqRecordBuilder(input.getId(),
							new NucleotideSequenceBuilder(input.getNucleotideSequence())
								.trim(trimmedRange)
								.build(),
							new QualitySequenceBuilder(input.getQualitySequence())
								.trim(trimmedRange)
								.build())
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
