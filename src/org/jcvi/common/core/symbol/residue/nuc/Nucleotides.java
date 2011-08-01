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

package org.jcvi.common.core.symbol.residue.nuc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dkatzel
 *
 *
 */
public final class Nucleotides {

    /**
     * Convert a list of {@link Nucleotide}s which may
     * contain {@link Nucleotide#Gap}s.
     * @param gapped a List of nucleotides which may contain gaps.
     * @return a new list of {@link Nucleotide}s which may be empty
     * but will never be null.
     * @throws NullPointerException if gapped is null.
     */
    public static List<Nucleotide> convertToUngapped(List<Nucleotide> gapped){
        List<Nucleotide> ungapped = new ArrayList<Nucleotide>(gapped.size());
        for(Nucleotide possibleGap : gapped){
            if(!possibleGap.isGap()){
                ungapped.add(possibleGap);
            }
        }
        return ungapped;
    }
    public static List<Nucleotide> getNucleotidesFor(char[] array){
       return getNucleotidesFor(new String(array));
    }
    public static List<Nucleotide> getNucleotidesFor(List<Character> list) {
        StringBuilder builder = new StringBuilder();
        for(Character c: list){
            builder.append(c);
        }
        return  getNucleotidesFor(builder);
    }
    public static List<Nucleotide> getNucleotidesFor(CharSequence s){
        List<Nucleotide> result = new ArrayList<Nucleotide>(s.length());
        try{
            for(int i=0; i<s.length(); i++){            
                result.add(Nucleotide.parse(s.charAt(i)));
            }
            return result;
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("could not getGlyphs for "+ s,e);
        }
        
    }

    public static String convertToString(List<Nucleotide> glyphs){
        StringBuilder result = new StringBuilder();
        for(Nucleotide g: glyphs){
            result.append(g.toString());
        }
        return result.toString();
    }
    
    /**
     * Given the input List of {@link Nucleotide}s
     * return the reverse compliment as a new List.
     * @param glyphs the {@link Nucleotide}s to reverse compliment.
     * @return the reverse compliment of the given List as a new List.
     */
    public static List<Nucleotide> reverseCompliment(List<Nucleotide> glyphs) {
        List<Nucleotide> reversed = new ArrayList<Nucleotide>(glyphs.size());
        for(int i=glyphs.size()-1; i>=0; i--){
            reversed.add(glyphs.get(i).compliment());
        }
        return reversed;
     }
}
