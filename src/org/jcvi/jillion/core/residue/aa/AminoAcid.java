/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.util.MapUtil;

/**
 * {@code AminoAcid} is a {@link Residue} representation 
 * of the 20 Amino Acids that are encoded by genetic code.
 * A Gap has also been included to support gapped
 * amino acid sequences.
 * 
 * @author dkatzel, naxelrod
 *
 *
 */
public enum AminoAcid implements Residue{
    
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
    Unknown_Amino_Acid("Unknown Amino Acid", "Uknown", 'X'),
    Gap("Gap", "Gap", '-')
    ;
    
    private final Character abbreviation;
    private final String threeLetterAbbreviation;
    private final String name;
    
    private static final Map<String, AminoAcid> NAME_MAP;
    static{
    	int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(AminoAcid.values().length *3);
        NAME_MAP = new HashMap<String, AminoAcid>(mapSize);
        
        for(AminoAcid aa : AminoAcid.values()){
            NAME_MAP.put(aa.getName().toUpperCase(), aa);
            NAME_MAP.put(aa.get3LetterAbbreviation().toUpperCase(), aa);
            NAME_MAP.put(aa.getCharacter().toString().toUpperCase(), aa);
        }
    }
    private AminoAcid(String name, String threeLetterAbbreviation, Character abbreviation){
        this.name = name;
        this.threeLetterAbbreviation = threeLetterAbbreviation;
        this.abbreviation = abbreviation;
    }
    /**
     * Get the AminoAcid which is represented by the given single character
     * abbreviation.  This is the same as {@link #parse(String)} parse(aminoAcidAbbreviation.toString()
     * @param aminoAcidAbbreviation the single character abbreviation of a aminoAcid.
     * @return an {@link AminoAcid} (not null).
     * @throws NullPointerException if aminoAcidAbbreviation is null.
     * @throws IllegalArgumentException if the given abbreviation is not
     * an AminoAcid.
     * @see #parse(String)
     */
    public static AminoAcid parse(Character aminoAcidAbbreviation){
        return parse(aminoAcidAbbreviation.toString());
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
    public static AminoAcid parse(String aminoAcid){
        AminoAcid result = NAME_MAP.get(aminoAcid.toUpperCase(Locale.US));
        if(result ==null){
            throw new IllegalArgumentException(String.format("%s is not a valid Amino Acid", aminoAcid));
        }
        return result;
    }
    
    /**
    * Get the full name of this Amino Acid, the name may 
    * contain spaces if the full name is more than 1 word.
    */
    public String getName() {
        return name;
    }
    
    /**
     * Returns this glyph as a single character String.  For example {@link #Alanine} 
     * will return "A".
     */
    @Override
    public String toString() {
        return getCharacter().toString();
    }
    
    /**
     * Get the 1 letter abbreviation for this Amino Acid
     * as a Character.
     * @return the 1 letter abbreviation for this Amino Acid
     * as a Character (not null).
     */
    @Override
    public Character getCharacter() {
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
     		result.append(g.getCharacter());
    	}
    	return result.toString();
    }
	@Override
	public byte getOrdinalAsByte() {
		return (byte)ordinal();
	}
	/**
     * Get the 1 letter abbreviation for this Amino Acid
     * as a char.
     * @return the 1 letter abbreviation for this Amino Acid
     * as a char.
     */
	public char asChar(){
		return abbreviation.charValue();
	}
    
}
