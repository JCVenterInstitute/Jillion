package org.jcvi.common.core.symbol.residue.aa;

public final class AminoAcids {

	private AminoAcids(){
		//private constructor
	}
	
	public static String asString(Iterable<AminoAcid> aminoAcidSequence){
		StringBuilder builder = new StringBuilder();
		for(AminoAcid aa : aminoAcidSequence){
			builder.append(aa.asChar());
		}
		return builder.toString();
	}
}
