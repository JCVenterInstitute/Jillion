package org.jcvi.jillion_experimental.align;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestAlnUtil {

	@Test
	public void validHeader(){
		assertTrue(AlnUtil.validHeader("CLUSTAL"));
		assertTrue(AlnUtil.validHeader("CLUSTAL W (1.82) multiple sequence alignment"));
		assertTrue(AlnUtil.validHeader("CLUSTALW (1.82) multiple sequence alignment"));
		
		assertTrue(AlnUtil.validHeader("CLUSTAL 2.0.11 multiple sequence alignment"));
		
	}
	
	@Test
	public void invalidHeader(){
		assertFalse(AlnUtil.validHeader("FOSB_MOUSE      MFQAFPGDYDSGSRCSSSPSAESQYLSSVDSFGSPPTAAASQECAGLGEMPGSFVPTVTA 60"));
		assertFalse(AlnUtil.validHeader(""));
	}
	@Test(expected = NullPointerException.class)
	public void nullHeaderShouldThrowNPE(){
		AlnUtil.validHeader(null);
	}
}
