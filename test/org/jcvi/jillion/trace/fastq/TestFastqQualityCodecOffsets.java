package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestFastqQualityCodecOffsets {

	@Test
	public void sangerIs33(){
		assertEquals(33, FastqQualityCodec.SANGER.getOffset());
	}
	@Test
	public void solexaIs64(){
		assertEquals(64, FastqQualityCodec.SOLEXA.getOffset());
	}
	@Test
	public void illuminaIs64(){
		assertEquals(64, FastqQualityCodec.ILLUMINA.getOffset());
	}
}
