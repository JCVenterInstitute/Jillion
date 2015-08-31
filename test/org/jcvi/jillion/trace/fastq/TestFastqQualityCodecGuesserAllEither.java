package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;

public class TestFastqQualityCodecGuesserAllEither {

	private final NucleotideSequence seq = new NucleotideSequenceBuilder("ACGTACGT").build();
	
	
	
	@Test
	public void allQvsSangerQv30() throws IOException{
		assertAll30sCorrectlyGuessed(FastqQualityCodec.SANGER);
	
	}
	@Test
	public void allQvsIlluminaQv30() throws IOException{
		assertAll30sCorrectlyGuessed(FastqQualityCodec.ILLUMINA);
	
	}


	private void assertAll30sCorrectlyGuessed(FastqQualityCodec codec)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FastqWriter writer = new FastqWriterBuilder(out)
											.qualityCodec(codec)
											.build();
		byte[] qvs = new byte[8];
		Arrays.fill(qvs, (byte)30);
		
		writer.write("id", seq, new QualitySequenceBuilder(qvs).build());
		writer.close();
		
		FastqParser parser = FastqFileParser.create(new ByteArrayInputStream(out.toByteArray()));
	
		assertEquals(codec, FastqUtil.guessQualityCodecUsed(parser));
	}
	
	 
}
