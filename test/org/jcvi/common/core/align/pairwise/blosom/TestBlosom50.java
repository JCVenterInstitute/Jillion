package org.jcvi.common.core.align.pairwise.blosom;

import org.jcvi.common.core.align.pairwise.AminoAcidScoringMatrix;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestBlosom50 {

	@Test
	public void spotCheck(){
		AminoAcidScoringMatrix blosom50 = BlosomMatrices.getMatrix(50);
		assertEquals(5F,
				blosom50.getScore(AminoAcid.Alanine, AminoAcid.Alanine),
				0F);
		
		assertEquals(10F,
				blosom50.getScore(AminoAcid.Proline, AminoAcid.Proline),
				0F);
		
		assertEquals(-3F,
				blosom50.getScore(AminoAcid.Proline, AminoAcid.Valine),
				0F);
		assertEquals(0F,
				blosom50.getScore(AminoAcid.Valine, AminoAcid.Threonine),
				0F);
	}
}
