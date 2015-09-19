/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import java.util.ArrayList;
import java.util.List;

final class AminoAcidUtil {

	private AminoAcidUtil(){
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
     * @return a List of AminoAcidUtil (not null), if the given String is empty,
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
