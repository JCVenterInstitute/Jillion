package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestGuessQualityCodec {

	@Test
	public void sanger(){
		String quals = "@;7C9;A)565A;4..9;2;45,?@###########################################################################";
		assertEquals(FastqQualityCodec.SANGER, FastqUtil.guessQualityCodecUsed(quals));
	}
	
	@Test
	public void illumina(){
		String quals = "`a\\a`^\\a^ZZa[]^WB_aaaa^^a`]^a`^`aaa`]``aXaaS^a^YaZaTW]a_aPY\\_UVY[P_ZHQY_NLZUR[^UZ\\TZWT_[_VWMWaRFW]BB";
		assertEquals(FastqQualityCodec.ILLUMINA, FastqUtil.guessQualityCodecUsed(quals));
	}
	
	@Test
	public void solexa(){
		//added negative values to end of string
		String quals = "`a\\a`^\\a^ZZa[]^WB_aaaa^^a`]^a`^`aaa`]``aXaaS^a^YaZaTW]a_aPY\\_UVY[P_ZHQY_NLZUR[^UZ\\TZWT_[_VWMWaRFW]BBAQ?>=";
		assertEquals(FastqQualityCodec.SOLEXA, FastqUtil.guessQualityCodecUsed(quals));

	}
}
