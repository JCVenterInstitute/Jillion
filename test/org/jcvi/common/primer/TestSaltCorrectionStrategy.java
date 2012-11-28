package org.jcvi.common.primer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class TestSaltCorrectionStrategy {

	@Test
	public void santaLucia(){
		assertEquals(20.737D, SaltCorrectionStrategy.SANTALUCIA.adjustTm(37, 50)
				,0.001D);
		assertEquals(24.5D, SaltCorrectionStrategy.SANTALUCIA.adjustTm(37, 100)
				,0.001D);
		assertEquals(37D, SaltCorrectionStrategy.SANTALUCIA.adjustTm(37, 1000)
				,0.001D);
	}
	@Test
	public void schildkrautLifson(){
		assertEquals(15.403D, SaltCorrectionStrategy.SCHILDKRAUT_LIFSON.adjustTm(37, 50)
				,0.001D);
		assertEquals(20.4D, SaltCorrectionStrategy.SCHILDKRAUT_LIFSON.adjustTm(37, 100)
				,0.001D);
		assertEquals(37D, SaltCorrectionStrategy.SCHILDKRAUT_LIFSON.adjustTm(37, 1000)
				,0.001D);
	}
	
}
