package org.jcvi.common.core.align.pairwise.blosom;

import org.jcvi.common.core.align.pairwise.AminoAcidScoringMatrix;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestBlosom50 {

	@Test
	public void spotCheck(){
		AminoAcidScoringMatrix blosom50 = BlosomMatrices.getMatrix(50);
		assertEquals(5F,
				blosom50.getScore(AminoAcid.Alanine.getOrdinalAsByte(), AminoAcid.Alanine.getOrdinalAsByte()),
				0F);
		
		assertEquals(10F,
				blosom50.getScore(AminoAcid.Proline.getOrdinalAsByte(), AminoAcid.Proline.getOrdinalAsByte()),
				0F);
		
		assertEquals(-3F,
				blosom50.getScore(AminoAcid.Proline.getOrdinalAsByte(), AminoAcid.Valine.getOrdinalAsByte()),
				0F);
		assertEquals(0F,
				blosom50.getScore(AminoAcid.Valine.getOrdinalAsByte(), AminoAcid.Threonine.getOrdinalAsByte()),
				0F);
	}
}
