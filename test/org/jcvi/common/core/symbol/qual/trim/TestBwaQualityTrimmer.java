package org.jcvi.common.core.symbol.qual.trim;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fastq.DefaultFastqRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastqDataStore;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecord;
import org.jcvi.common.core.seq.fastx.fastq.LargeFastqFileDataStore;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestBwaQualityTrimmer {

	private final FastqDataStore inputFastq, outputFastq;
	private final BwaQualityTrimmer sut = new BwaQualityTrimmer(PhredQuality.valueOf(20));
	public TestBwaQualityTrimmer() throws FileNotFoundException, IOException{
		ResourceFileServer resources = new ResourceFileServer(TestBwaQualityTrimmer.class);
		inputFastq = LargeFastqFileDataStore.create(resources.getFile("files/bwa_input.fastq"), FastqQualityCodec.SANGER);
		outputFastq = LargeFastqFileDataStore.create(resources.getFile("files/bwa_output.fastq"), FastqQualityCodec.SANGER);
		
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
					FastqRecord trimmedSequence = new DefaultFastqRecord(input.getId(),
							new NucleotideSequenceBuilder(input.getNucleotideSequence())
								.trim(trimmedRange)
								.build(),
							new QualitySequenceBuilder(input.getQualitySequence())
								.trim(trimmedRange)
								.build());
					assertEquals(trimmedSequence.getId(),expectedIterator.next(), trimmedSequence);
				}
			}
			assertFalse(expectedIterator.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(inputIterator,expectedIterator);
		}
	}
	
}
