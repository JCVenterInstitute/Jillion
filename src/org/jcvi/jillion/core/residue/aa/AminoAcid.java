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

import java.util.*;
import java.util.regex.Pattern;

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
    //ambiguities
    Unknown_Amino_Acid("Unknown Amino Acid", "Unk", 'X'),
    Aspartate_or_Asparagine("Aspartate or Asparagine", "Asx", 'B'),
    Glutamate_or_Glutamine("Glutamate or Glutamine", "Glx", 'Z'),
    //Selenocysteine - inserted as a post-translational modification
    Selenocysteine("Selenocysteine", "Sec", 'U'),
    
    Pyrrolysine("Pyrrolysine", "Ply",'O'),
    Gap("Gap", "---", '-', false){

		@Override
		public boolean isGap() {
			return true;
		}
    	
    },
    
    STOP("Stop", "Stop", '*', false),

    Leucine_or_Isoleucine("Leucine or Isoleucine", "Xle", 'J')
    ;
    
    private final Character abbreviation;
    private final String threeLetterAbbreviation;
    private final String name;

    private final boolean includeInPattern;

    private static final Pattern CLEAN_PATTERN;
    private static final Map<String, AminoAcid> NAME_MAP;
    
    private static final Map<Set<AminoAcid>, AminoAcid> AMBIGIOUS_MAP;
    static{
    	int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(AminoAcid.values().length *3);
        NAME_MAP = new HashMap<>(mapSize);
        StringBuilder validBuilder = new StringBuilder();
        validBuilder.append("[^");
        for(AminoAcid aa : AminoAcid.values()){
            NAME_MAP.put(aa.getName().toUpperCase(), aa);
            NAME_MAP.put(aa.get3LetterAbbreviation().toUpperCase(), aa);

            String charAsString = aa.getCharacter().toString();
            String upperCase = charAsString.toUpperCase();

            NAME_MAP.put(upperCase, aa);
            if(aa.includeInPattern) {
                validBuilder.append(upperCase);
                validBuilder.append(charAsString.toLowerCase());
            }

        }
        validBuilder.append("\\-\\s]");
        CLEAN_PATTERN = Pattern.compile(validBuilder.toString());
        
        AMBIGIOUS_MAP = new HashMap<>();
        
        AMBIGIOUS_MAP.put(EnumSet.of(Isoleucine, Leucine), AminoAcid.Leucine_or_Isoleucine);
        AMBIGIOUS_MAP.put(EnumSet.of(Leucine_or_Isoleucine, Leucine), AminoAcid.Leucine_or_Isoleucine);
        AMBIGIOUS_MAP.put(EnumSet.of(Leucine_or_Isoleucine, Isoleucine), AminoAcid.Leucine_or_Isoleucine);
        
        AMBIGIOUS_MAP.put(EnumSet.of(Aspartic_Acid, Asparagine), AminoAcid.Aspartate_or_Asparagine);
        AMBIGIOUS_MAP.put(EnumSet.of(Aspartate_or_Asparagine, Asparagine), AminoAcid.Aspartate_or_Asparagine);
        AMBIGIOUS_MAP.put(EnumSet.of(Aspartate_or_Asparagine, Aspartic_Acid), AminoAcid.Aspartate_or_Asparagine);
        
        AMBIGIOUS_MAP.put(EnumSet.of(Glutamic_Acid, Glutamine), AminoAcid.Glutamate_or_Glutamine);
        AMBIGIOUS_MAP.put(EnumSet.of(Glutamate_or_Glutamine, Glutamine), AminoAcid.Glutamate_or_Glutamine);
        AMBIGIOUS_MAP.put(EnumSet.of(Glutamate_or_Glutamine, Glutamic_Acid), AminoAcid.Glutamate_or_Glutamine);
        
    
    
    }
    /**
     * Get the AminoAcid that best represents the given group of amino acids.
     * @param aas the amino acids to consider; can not be null or contain any null elements;
     * @return
     */
    public static AminoAcid merge(Iterable<AminoAcid> aas) {
    	Set<AminoAcid> set =  EnumSet.noneOf(AminoAcid.class);
    	AminoAcid first=null;
    	for(AminoAcid aa : aas) {
    		if(aa==null) {
    			throw new NullPointerException("amino acid can not be null");
    		}
    		if(first==null) {
    			first=aa;
    		}
    		set.add(aa);
    	}
    	
    	if(set.size()==1) {
    		return first;
    	}
    	return AMBIGIOUS_MAP.getOrDefault(set, AminoAcid.Unknown_Amino_Acid);
    }
    /**
     * Is This AminoAcid an ambiguity?
     * An ambiguity is X, B, Z, and J.
     * @return {@code true} if it is am ambiguity;
     * {@code false} otherwise.
     */
    @Override
    public boolean isAmbiguity(){
        return this == Unknown_Amino_Acid || this == Aspartate_or_Asparagine
                || this == Leucine_or_Isoleucine || this == Glutamate_or_Glutamine;
    }
    /**
     * Remove all non-valid, non-whitespace characters in the given input sequence.
     * This is the same as {@link #cleanSequence(String, String) cleanSequence(seq, ""}
     * @param seq the input sequence to clean; can not be null.
     * @return a new String that is the same as the input sequence
     * except each invalid character has been removed.
     *
     * @throws NullPointerException if either parameter is null.
     *
     * @since 5.3.1
     * @see #cleanSequence(String, String)
     */
    public static String cleanSequence(String seq){
        return cleanSequence(seq,"");
    }

    /**
     * Replace all non-valid, non-whitespace characters in the given input sequence
     * with the given replacement string.
     * @param seq the input sequence to clean; can not be null.
     * @param replacementString the String to use for EACH invalid character;
     *                          can not be null.
     * @return a new String that is the same as the input sequence
     * except each invalid character gets replaced by the replacement String.
     *
     * @throws NullPointerException if either parameter is null.
     *
     * @since 5.3.1
     */
    public static String cleanSequence(String seq, String replacementString){
        return CLEAN_PATTERN.matcher(seq).replaceAll(Objects.requireNonNull(replacementString));
    }
    private AminoAcid(String name, String threeLetterAbbreviation, Character abbreviation){
        this(name, threeLetterAbbreviation,abbreviation, true);
    }
    private AminoAcid(String name, String threeLetterAbbreviation, Character abbreviation,
                      boolean includeInClean){
        this.name = name;
        this.threeLetterAbbreviation = threeLetterAbbreviation;
        this.abbreviation = abbreviation;
        includeInPattern = includeInClean;
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
     * @param aminoAcid a single AminoAcid represented by a String or {@code null} if not a valid amino acid.
     * @return an {@link AminoAcid} (not null).
     * @throws NullPointerException if aminoAcid is null.
     *
     * @since 5.3.3
     */
    public static AminoAcid safeParse(String aminoAcid) {
        return NAME_MAP.get(aminoAcid.toUpperCase(Locale.US));
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
    * (ex "Aspartic Acid").
    * 
    * @return the full name of this amino acid.
    */
    public String getName() {
        return name;
    }
    
    
    /**
     * Is this amino acid a gap?
     * 
     * @return {@code false} unless this is {@link AminoAcid#Gap}.
     */
    @Override
	public boolean isGap() {
		return false;
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
    /**
     * Append the list of amino acids into one long string.
     * @param glyphs the amino acids to convert into a string.
     * @return a new String.
     */
    public static String convertToString(List<AminoAcid> glyphs){
    	StringBuilder result = new StringBuilder(glyphs.size());
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
