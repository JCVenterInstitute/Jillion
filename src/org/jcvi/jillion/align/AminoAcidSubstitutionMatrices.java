package org.jcvi.jillion.align;

public class AminoAcidSubstitutionMatrices {


	private static final AminoAcidSubstitutionMatrix IDENTITY = new AminoAcidSubstitutionMatrixBuilder(-4)
			.setMatch(1)
			.build();
	
	public static AminoAcidSubstitutionMatrix getIdentityMatrix() {
		return IDENTITY;
	}
}
