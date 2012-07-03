package org.jcvi.common.core.align.pairwise.blosom;


import org.jcvi.common.core.align.pairwise.AminoAcidScoringMatrix;
import org.jcvi.common.core.align.pairwise.PropertyFileAminoAcidScoringMatrix;

public final class BlosomMatrices {

	private BlosomMatrices(){
		//private constructor.
	}
	public static final AminoAcidScoringMatrix getMatrix(int value){
		String file = String.format("blosom%d.matrix",value);
		return new PropertyFileAminoAcidScoringMatrix(
				BlosomMatrices.class.getResourceAsStream(file));
	}
}
