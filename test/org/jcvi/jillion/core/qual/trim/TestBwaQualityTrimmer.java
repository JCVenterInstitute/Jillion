package org.jcvi.jillion.core.qual.trim;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.trace.fastq.FastqDataStore;
import org.jcvi.common.core.seq.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.common.core.seq.trace.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.trace.fastq.FastqRecord;
import org.jcvi.common.core.seq.trace.fastq.FastqRecordBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.trim.BwaQualityTrimmer;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
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
