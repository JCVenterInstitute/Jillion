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

package org.jcvi.common.core.symbol.residue.nt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@code Nucleotides} is a helper class
 * that works with Collections of {@link Nucleotide}
 * objects.
 * @author dkatzel
 *
 */
public final class Nucleotides {
	/**
	 * Pattern to detect whitespace, used in removing whitespace from strings.
	 * This has been refactored out because calling
	 * {@link String#replaceAll(String, String)}
	 * recompiles the pattern for every invocation.
	 */
	private static Pattern WHITESPACE = Pattern.compile("\\s+");
    /**
     * Can not instantiate.
     */
    private Nucleotides(){
        throw new IllegalStateException("not allowed to instantiate");
    }
    /**
     * Creates a new list of {@link Nucleotide}s which is the
     * same as the input list except all the {@link Nucleotide#Gap}
     * objects have been removed.
     * @param gapped a List of nucleotides which may contain gaps.
     * @return a new list of {@link Nucleotide}s which may be empty
     * but will never be null.
     * @throws NullPointerException if gapped is null.
     */
    public static List<Nucleotide> ungap(List<Nucleotide> gapped){
        List<Nucleotide> ungapped = new ArrayList<Nucleotide>(gapped.size());
        for(Nucleotide possibleGap : gapped){
            if(!possibleGap.isGap()){
                ungapped.add(possibleGap);
            }
        }
        return ungapped;
    }
    /**
     * Parse the given char array containing
     * nucleotides and return an equivalent List
     * of {@link Nucleotide}s.
     * @param array the char array to parse, may have
     * leading and/or trailing whitespace.
     * @return a new List will never be null.
     * @throws NullPointerException if nucleotides is null.
     * @throws IllegalArgumentException if there is a
     * character in the nucleotides aside from leading or trailing 
     * whitespace that can not be parsed into a Nucleotide.     *
     */
    public static List<Nucleotide> parse(char[] array){
       return parse(new String(array));
    }
    /**
     * Parse the given Collection of Characters containing
     * nucleotides and return an equivalent List
     * of {@link Nucleotide}s.  Any whitespace characters
     * will be ignored.
     * @param chars the collection of characters to parse, may have
     * whitespace which will be ignored.
     * @return a new List will never be null.
     * @throws NullPointerException if nucleotides is null.
     * @throws IllegalArgumentException if there is a
     * character in the nucleotides aside from 
     * whitespace that can not be parsed into a Nucleotide.
     *
     */
    public static List<Nucleotide> parse(Collection<Character> chars) {
        List<Nucleotide> result = new ArrayList<Nucleotide>(chars.size());        
        for(Character c: chars){
            if(!Character.isWhitespace(c)){
                result.add(Nucleotide.parse(c));
            }
            
        }
        return  result;
    }
    /**
     * Parse the given {@link CharSequence} containing
     * nucleotides and return an equivalent List
     * of {@link Nucleotide}s.
     * @param nucleotides the charSequence to parse, may have
     * whitespace which will be ignored.
     * @return a new List will never be null.
     * @throws NullPointerException if nucleotides is null.
     * @throws IllegalArgumentException if there is a
     * character in the nucleotides aside from leading or trailing 
     * whitespace that can not be parsed into a Nucleotide.
     *
     */
    public static List<Nucleotide> parse(CharSequence nucleotides){
        String trimmed = WHITESPACE.matcher(nucleotides).replaceAll("");
        List<Nucleotide> result = new ArrayList<Nucleotide>(trimmed.length());
        try{
            for(int i=0; i<trimmed.length(); i++){            
                result.add(Nucleotide.parse(trimmed.charAt(i)));
            }
            return result;
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("could not parse "+ nucleotides,e);
        }
        
    }
    /**
     * Convert the given {@link Iterable} of nucleotides
     * into a String which represents the same
     * nucleotide sequence.
     * @param nucleotides the nucleotides to convert to a string
     * @return a new String, will never be null.
     * @throws NullPointerException if nucleotides is null.
     */
    public static String asString(Iterable<Nucleotide> nucleotides){
        StringBuilder result = new StringBuilder();
        for(Nucleotide g: nucleotides){
            result.append(g.toString());
        }
        return result.toString();
    }
    
    /**
     * Given the input List of {@link Nucleotide}s
     * return the reverse complement as a new List.
     * @param nucleotides the {@link Nucleotide}s to reverse complement.
     * @return the reverse complement of the given List as a new List.
     */
    public static List<Nucleotide> reverseComplement(List<Nucleotide> nucleotides) {
        List<Nucleotide> reversed = new ArrayList<Nucleotide>(nucleotides.size());
        for(int i=nucleotides.size()-1; i>=0; i--){
            reversed.add(nucleotides.get(i).complement());
        }
        return reversed;
     }
}
