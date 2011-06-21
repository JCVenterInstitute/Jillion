/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.glyph.aa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.glyph.Glyph;

/**
 * {@code AminoAcid} is a {@link Glyph} representation 
 * of the 20 Amino Acids that are encoded by genetic code.
 * 
 * @author dkatzel, naxelrod
 *
 *
 */
public enum AminoAcid implements Glyph{
    
    Isoleucine("Isolucine","Ile",'I'),
    Leucine("Leucine","Leu",'L'),
    Lysine("Lysine","Lys",'K'),
    Methionine("Methionine","Met",'M'),
    Phenylalanine("Phenylalanine","Phe",'F'),
    Threonine("Threonine","Thr",'T'),
    Tryptophan("Tryptophan","Trp",'W'),
    Valine("Valine","Val",'V'),
    Cysteine("Cysteine","Cys",'C'),
    Glutamine("Glutamine","Gln",'Q'),
    Glycine("Glycine","Gly", 'G'),
    Proline("Proline","Pro",'P'),
    Serine("Serine","Ser",'S'),
    Tyrosine("Tyrosine","Tyr", 'Y'),
    Arginine("Arginine","Arg",'R'),
    Histidine("Histidine","His",'H'),
    Alanine("Alanine","Ala",'A'),
    Asparagine("Asparagine","Asn",'N'),
    Aspartic_Acid("Aspartic Acid", "Asp",'D'),
    Glutamic_Acid("Glutamic Acid","Glu",'E'),
    Unknown_Amino_Acid("Unknown Amino Acid", "Uknown", 'X')
    ;
    
    private final Character abbreviation;
    private final String threeLetterAbbreviation;
    private final String name;
    
    private static final Map<String, AminoAcid> NAME_MAP;
    static{
        NAME_MAP = new HashMap<String, AminoAcid>(60,1F);
        
        for(AminoAcid aa : AminoAcid.values()){
            NAME_MAP.put(aa.getName().toUpperCase(), aa);
            NAME_MAP.put(aa.get3LetterAbbreviation().toUpperCase(), aa);
            NAME_MAP.put(aa.getAbbreviation().toString().toUpperCase(), aa);
        }
    }
    private AminoAcid(String name, String threeLetterAbbreviation, Character abbreviation){
        this.name = name;
        this.threeLetterAbbreviation = threeLetterAbbreviation;
        this.abbreviation = abbreviation;
    }
    /**
     * Get the AminoAcid which is represented by the given single character
     * abbreviation.  This is the same as {@link #getGlyphFor(String)} getGlyphFor(aminoAcidAbbreviation.toString()
     * @param aminoAcid the single character abbreviation of a aminoAcid.
     * @return an {@link AminoAcid} (not null).
     * @throws NullPointerException if aminoAcidAbbreviation is null.
     * @throws IllegalArgumentException if the given abbreviation is not
     * an AminoAcid.
     * @see #getGlyphFor(String)
     */
    public static AminoAcid getGlyphFor(Character aminoAcidAbbreviation){
        return getGlyphFor(aminoAcidAbbreviation.toString());
    }
    /**
     * Get the single AminoAcid which is represented by the given String.
     * the String can be the full name (with spaces if multiple words),
     * the 3 letter abbreviation or the 1 letter abbreviation, case is 
     * insensitive.
     * @param aminoAcid a single AminoAcid represented by a String.
     * @return an {@link AminoAcid} (not null).
     * @throws NullPointerException if aminoAcid is null.
     * @throws IllegalArgumentException if the given String is not
     * an AminoAcid.
     */
    public static AminoAcid getGlyphFor(String aminoAcid){
        AminoAcid result = NAME_MAP.get(aminoAcid.toUpperCase());
        if(result ==null){
            throw new IllegalArgumentException(String.format("%s is not a valid Amino Acid", aminoAcid));
        }
        return result;
    }
    /**
     * Convert a String of many 1 letter Amino Acid Abbreviations
     * into a List of {@link AminoAcid}s.
     * <p>
     * For example:
     * <p>
     * {@code getGlyphsFor("ILW") => [Isoleucine, Leucine, Tryptophan]}
     * @param aminoAcids a String where each character is a 1 letter
     * abbreviation of an Amino Acid.
     * @return a List of AminoAcids (not null), if the given String is empty,
     * then an Empty List is returned.
     * @throws NullPointerException if the given String is null.
     * 
     */
    public static List<AminoAcid> getGlyphsFor(String aminoAcids){
        List<AminoAcid> result = new ArrayList<AminoAcid>(aminoAcids.length());
        for(int i=0; i<aminoAcids.length(); i++){
            result.add(getGlyphFor(aminoAcids.charAt(i)));
        }
        return result;
    }
    /**
    * Get the full name of this Amino Acid, the name may 
    * contain spaces if the full name is more than 1 word.
    */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Returns this glyph as a single character String.  For example {@link #Adenine} 
     * will return "A".
     */
    @Override
    public String toString() {
        return getAbbreviation().toString();
    }
    
    /**
     * Get the 1 letter abbreviation for this Amino Acid
     * as a Character.
     * @return the 1 letter abbreviation for this Amino Acid
     * as a Character (not null).
     */
    public Character getAbbreviation() {
        return abbreviation;
    }
    /**
     * Get the 3 letter abbreviation for this Amino Acid.
     * @return the 3 letter abbrevation for this Amino Acid (not null).
     */
    public String get3LetterAbbreviation() {
        return threeLetterAbbreviation;
    }
    
    public static String convertToString(List<AminoAcid> glyphs){
    	StringBuilder result = new StringBuilder();
    	for(AminoAcid g: glyphs){
     		result.append(g.getAbbreviation());
    	}
    	return result.toString();
    }

}
