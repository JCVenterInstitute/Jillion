package org.jcvi.common.core.symbol.residue.aa;

import java.util.ArrayList;
import java.util.List;

public final class AminoAcids {

	private AminoAcids(){
		//private constructor
	}
	/**
     * Parses a String of many 1 letter Amino Acid Abbreviations
     * into a List of {@link AminoAcid}s.
     * <p>
     * For example:
     * <p>
     * {@code parse("ILW") => [Isoleucine, Leucine, Tryptophan]}
     * @param aminoAcids a String where each character is a 1 letter
     * abbreviation of an Amino Acid.
     * @return a List of AminoAcids (not null), if the given String is empty,
     * then an Empty List is returned.
     * @throws NullPointerException if the given String is null.
     * 
     */
	public static List<AminoAcid> parse(String aminoAcids){
		List<AminoAcid> result = new ArrayList<AminoAcid>(aminoAcids.length());
        for(int i=0; i<aminoAcids.length(); i++){
            char charAt = aminoAcids.charAt(i);
            if(!Character.isWhitespace(charAt)){
            	result.add(AminoAcid.parse(charAt));
            }
        }
        return result;
	}
	
	public static String asString(Iterable<AminoAcid> aminoAcidSequence){
		StringBuilder builder = new StringBuilder();
		for(AminoAcid aa : aminoAcidSequence){
			builder.append(aa.asChar());
		}
		return builder.toString();
	}
}
